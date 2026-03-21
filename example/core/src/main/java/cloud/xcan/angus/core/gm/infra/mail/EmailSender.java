package cloud.xcan.angus.core.gm.infra.mail;

import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailSmtp;
import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * 邮件发送器 负责根据SMTP配置初始化JavaMailSender实例，并提供发送邮件和测试连接的功能
 */
@Slf4j
@Component
public class EmailSender {

  private volatile JavaMailSender mailSender;
  private volatile EmailSmtp currentSmtpConfig;

  public EmailSender() {
  }

  /**
   * 根据SMTP配置初始化JavaMailSender实例
   *
   * @param smtp SMTP配置
   * @return JavaMailSender实例
   */
  public JavaMailSender initMailSender(EmailSmtp smtp) {
    if (smtp == null) {
      throw new IllegalArgumentException("SMTP配置不能为空");
    }

    // 如果配置未变化，直接返回缓存的实例
    if (mailSender != null && currentSmtpConfig != null
        && currentSmtpConfig.getId().equals(smtp.getId())
        && currentSmtpConfig.getModifiedDate() != null
        && smtp.getModifiedDate() != null
        && currentSmtpConfig.getModifiedDate().equals(smtp.getModifiedDate())) {
      return mailSender;
    }

    JavaMailSenderImpl sender = new JavaMailSenderImpl();
    sender.setHost(smtp.getHost());
    sender.setPort(smtp.getPort());
    sender.setUsername(smtp.getUsername());
    sender.setPassword(smtp.getPassword());

    Properties props = sender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    // SSL 和 STARTTLS 互斥，优先使用明确指定的配置
    boolean useSsl = Boolean.TRUE.equals(smtp.getUseSsl());
    boolean useStartTls = Boolean.TRUE.equals(smtp.getUseStartTls());
    // 如果两者都为 true，优先使用 SSL
    if (useSsl && useStartTls) {
      log.warn("SSL 和 STARTTLS 不能同时启用，将优先使用 SSL。SMTP配置ID: {}", smtp.getId());
      useStartTls = false;
    }
    props.put("mail.smtp.ssl.enable", useSsl ? "true" : "false");
    props.put("mail.smtp.starttls.enable", useStartTls ? "true" : "false");
    props.put("mail.smtp.connectiontimeout", "10000");
    props.put("mail.smtp.timeout", "10000");
    props.put("mail.smtp.writetimeout", "10000");
    props.put("mail.debug", "false");

    // 更新缓存
    synchronized (this) {
      mailSender = sender;
      currentSmtpConfig = smtp;
    }
    return sender;
  }

  /**
   * 测试SMTP连接
   *
   * @param smtp SMTP配置
   * @return true表示连接成功，false表示连接失败
   * @throws MessagingException 连接异常
   */
  public boolean testConnection(EmailSmtp smtp) throws MessagingException {
    if (smtp == null) {
      throw new IllegalArgumentException("SMTP配置不能为空");
    }

    Properties props = new Properties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.host", smtp.getHost());
    props.put("mail.smtp.port", smtp.getPort());
    // SSL 和 STARTTLS 互斥，优先使用明确指定的配置
    boolean useSsl = Boolean.TRUE.equals(smtp.getUseSsl());
    boolean useStartTls = Boolean.TRUE.equals(smtp.getUseStartTls());
    props.put("mail.smtp.auth", useSsl || useStartTls ? "true" : "false");
    // 如果两者都为 true，优先使用 SSL
    if (useSsl && useStartTls) {
      log.warn("SSL 和 STARTTLS 不能同时启用，将优先使用 SSL。SMTP Host: {}", smtp.getHost());
      useStartTls = false;
    }
    props.put("mail.smtp.ssl.enable", useSsl ? "true" : "false");
    props.put("mail.smtp.starttls.enable", useStartTls ? "true" : "false");
    props.put("mail.smtp.connectiontimeout", "5000");
    props.put("mail.smtp.timeout", "5000");
    props.put("mail.smtp.writetimeout", "5000");
    props.put("mail.debug", "false");

    Session session = Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(smtp.getUsername(), smtp.getPassword());
      }
    });

    Transport transport = null;
    try {
      transport = session.getTransport("smtp");
      transport.connect(smtp.getHost(), smtp.getPort(), smtp.getUsername(), smtp.getPassword());
      return true;
    } finally {
      if (transport != null) {
        try {
          transport.close();
        } catch (MessagingException e) {
          log.warn("关闭SMTP连接失败", e);
        }
      }
    }
  }

  /**
   * 发送邮件
   *
   * @param email 邮件实体
   * @param smtp  SMTP配置
   * @throws MessagingException 发送异常
   */
  public void sendEmail(Email email, EmailSmtp smtp) throws MessagingException {
    if (email == null) {
      throw new IllegalArgumentException("邮件不能为空");
    }
    if (smtp == null) {
      throw new IllegalArgumentException("SMTP配置不能为空");
    }

    // 初始化JavaMailSender
    JavaMailSender mailSender = initMailSender(smtp);

    // 创建MimeMessage
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

    // 设置发件人
    String fromName = smtp.getFromName();
    String fromEmail = smtp.getFromEmail();
    try {
      if (fromName != null && !fromName.isEmpty()) {
        helper.setFrom(new InternetAddress(fromEmail, fromName, "UTF-8"));
      } else {
        helper.setFrom(fromEmail);
      }
    } catch (UnsupportedEncodingException e) {
      log.warn("设置发件人名称编码失败，使用默认编码", e);
      helper.setFrom(fromEmail);
    }

    // 设置收件人
    if (email.getToRecipients() != null && !email.getToRecipients().isEmpty()) {
      InternetAddress[] toAddresses = email.getToRecipients().stream()
          .map(address -> {
            try {
              return new InternetAddress(address);
            } catch (Exception e) {
              log.warn("无效的收件人邮箱地址: {}", address, e);
              return null;
            }
          })
          .filter(Objects::nonNull)
          .toArray(InternetAddress[]::new);
      if (toAddresses.length > 0) {
        helper.setTo(toAddresses);
      }
    }

    // 设置抄送
    if (email.getCcRecipients() != null && !email.getCcRecipients().isEmpty()) {
      InternetAddress[] ccAddresses = email.getCcRecipients().stream()
          .map(address -> {
            try {
              return new InternetAddress(address);
            } catch (Exception e) {
              log.warn("无效的抄送邮箱地址: {}", address, e);
              return null;
            }
          })
          .filter(Objects::nonNull)
          .toArray(InternetAddress[]::new);
      if (ccAddresses.length > 0) {
        helper.setCc(ccAddresses);
      }
    }

    // 设置密送
    if (email.getBccRecipients() != null && !email.getBccRecipients().isEmpty()) {
      InternetAddress[] bccAddresses = email.getBccRecipients().stream()
          .map(address -> {
            try {
              return new InternetAddress(address);
            } catch (Exception e) {
              log.warn("无效的密送邮箱地址: {}", address, e);
              return null;
            }
          })
          .filter(Objects::nonNull)
          .toArray(InternetAddress[]::new);
      if (bccAddresses.length > 0) {
        helper.setBcc(bccAddresses);
      }
    }

    // 设置回复地址
    if (email.getReplyTo() != null && !email.getReplyTo().isEmpty()) {
      helper.setReplyTo(email.getReplyTo());
    }

    // 设置主题
    helper.setSubject(email.getSubject() != null ? email.getSubject() : "");

    // 设置邮件内容
    if (email.getHtmlContent() != null && !email.getHtmlContent().isEmpty()) {
      helper.setText(email.getHtmlContent(), true);
    } else if (email.getTextContent() != null && !email.getTextContent().isEmpty()) {
      helper.setText(email.getTextContent(), false);
    } else {
      throw new IllegalArgumentException("邮件内容不能为空");
    }

    // 设置附件
    if (email.getAttachments() != null && !email.getAttachments().isEmpty()) {
      for (Map<String, Object> attachment : email.getAttachments()) {
        String fileName = (String) attachment.get("fileName");
        String fileUrl = (String) attachment.get("fileUrl");

        if (fileName != null && fileUrl != null) {
          try {
            // 从URL读取文件流（lambda中打开流，确保在发送时流是打开的）
            URI uri = URI.create(fileUrl);
            helper.addAttachment(fileName, () -> {
              try {
                return uri.toURL().openStream();
              } catch (Exception e) {
                log.error("打开附件流失败: fileName={}, fileUrl={}", fileName, fileUrl, e);
                throw new RuntimeException("无法读取附件: " + fileName, e);
              }
            });
          } catch (Exception e) {
            log.warn("添加附件失败: fileName={}, fileUrl={}", fileName, fileUrl, e);
            // 继续处理其他附件，不中断发送流程
          }
        }
      }
    }

    // 设置优先级
    if (email.getPriority() != null) {
      mimeMessage.setHeader("X-Priority", String.valueOf(email.getPriority()));
    }

    // 发送邮件
    mailSender.send(mimeMessage);

    log.info("邮件发送成功: subject={}, to={}", email.getSubject(), email.getToRecipients());
  }

}
