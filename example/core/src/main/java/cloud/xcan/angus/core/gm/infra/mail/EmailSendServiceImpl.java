package cloud.xcan.angus.core.gm.infra.mail;

import cloud.xcan.angus.api.commonlink.email.EmailStatus;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.query.email.EmailQuery;
import cloud.xcan.angus.core.gm.application.query.email.EmailSmtpQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailSmtp;
import cloud.xcan.angus.core.utils.PrincipalContextUtils;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 邮件发送服务实现
 */
@Slf4j
@Service
public class EmailSendServiceImpl implements EmailSendService {

  @Resource
  private EmailQuery emailQuery;

  @Resource
  private EmailCmd emailCmd;

  @Resource
  private EmailSmtpQuery emailSmtpQuery;

  @Resource
  private EmailSender emailSender;

  @Override
  @Async
  public void sendEmailAsync(Long emailId) {
    sendEmail(emailId);
  }

  @Override
  @Async
  public void sendEmailAsync(Email email) {
    sendEmail(email);
  }

  @Override
  public void sendEmail(Long emailId) {
    try {
      // 查询邮件
      Email email = emailQuery.findAndCheck(emailId);
      sendEmail(email);
    } catch (Exception e) {
      log.error("处理邮件「{}」发送时发生异常", emailId, e);
    }
  }

  @Override
  public void sendEmail(Email email) {
    try {
      // 检查邮件状态
      if (email.getStatus() != EmailStatus.SENDING && email.getStatus() != EmailStatus.PENDING) {
        log.warn("邮件「{}」状态为「{}」，无法发送", email.getId(), email.getStatus());
        return;
      }

      // 设置租户上下文
      Long tenantId = email.getTenantId();
      PrincipalContextUtils.setMultiTenantCtrl(false);
      PrincipalContext.get().setTenantId(tenantId);

      try {
        // 获取SMTP配置
        EmailSmtp smtp = emailSmtpQuery.findDefault()
            .orElseThrow(() -> new IllegalStateException("未配置SMTP服务器"));

        // 发送邮件
        emailSender.sendEmail(email, smtp);

        // 更新邮件状态为已发送
        email.setStatus(EmailStatus.SENT);
        email.setSendTime(LocalDateTime.now());
        emailCmd.update0(email);

        log.info("邮件「{}」发送成功", email.getId());
      } catch (Exception e) {
        log.error("邮件「{}」发送失败", email.getId(), e);
        // 更新邮件状态为失败
        email.setStatus(EmailStatus.FAILED);
        email.setErrorCode(e.getClass().getSimpleName());
        email.setErrorMessage(e.getMessage() != null ? e.getMessage() : e.getClass().getName());
        emailCmd.update0(email);
      } finally {
        PrincipalContextUtils.setMultiTenantCtrl(true);
      }
    } catch (Exception e) {
      log.error("处理邮件「{}」发送时发生异常", email.getId(), e);
    }
  }
}
