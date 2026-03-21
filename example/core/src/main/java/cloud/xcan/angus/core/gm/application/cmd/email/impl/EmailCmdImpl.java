package cloud.xcan.angus.core.gm.application.cmd.email.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotEmpty;
import static cloud.xcan.angus.core.gm.application.converter.EmailConverter.replaceTemplateParamsWithObject;
import static cloud.xcan.angus.core.gm.application.converter.EmailConverter.toTemplateEmail;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.commonlink.email.EmailStatus;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.query.email.EmailQuery;
import cloud.xcan.angus.core.gm.application.query.email.EmailTemplateQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailRepo;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;
import cloud.xcan.angus.core.gm.infra.mail.EmailSendService;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.ProtocolException;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailCmdImpl extends CommCmd<Email, Long> implements EmailCmd {

  @Resource
  private EmailRepo emailRepo;

  @Resource
  private EmailQuery emailQuery;

  @Resource
  private EmailTemplateQuery emailTemplateQuery;

  @Resource
  private EmailSendService emailSendService;

  @Resource
  private EmailCmd self;

  @Resource
  private UserQuery userQuery;

  @Override
  @Transactional
  public Email create(Email email) {
    return new BizTemplate<Email>() {
      EmailTemplate template;

      @Override
      protected void checkParams() {
        // 如果提供了模板ID和模板参数，则查找模板
        if (email.getTemplateId() != null && email.getTemplateParams() != null
            && !email.getTemplateParams().isEmpty()) {
          template = emailTemplateQuery.findAndCheck(email.getTemplateId());
        }
      }

      @Override
      protected Email process() {
        // 如果提供了模板信息，则根据模板解析主题和内容
        if (template != null && email.getTemplateParams() != null
            && !email.getTemplateParams().isEmpty()) {
          // 替换模板主题中的参数
          String subject = replaceTemplateParamsWithObject(template.getSubject(),
              email.getTemplateParams());
          // 替换模板内容中的参数
          String htmlContent = replaceTemplateParamsWithObject(template.getContent(),
              email.getTemplateParams());

          email.setSubject(subject);
          email.setHtmlContent(htmlContent);
        }

        email.setStatus(EmailStatus.PENDING);
        email.setRetryCount(0);
        if (email.getMaxRetry() == null) {
          email.setMaxRetry(3);
        }
        if (getOptTenantId() < 1) {
          email.setTenantId(userQuery.findTenantIdByEmail(email.getToRecipients().get(0)));
        } else {
          email.setTenantId(getOptTenantId());
        }
        return insert(email);
      }
    }.execute();
  }

  @Override
  @Transactional
  public Email send(Long id, boolean async) {
    return new BizTemplate<Email>() {
      Email existing;

      @Override
      protected void checkParams() {
        existing = emailQuery.findAndCheck(id);
      }

      @Override
      protected Email process() {
        existing.setStatus(EmailStatus.PENDING);
        existing.setSendTime(LocalDateTime.now());
        emailRepo.save(existing);

        if (async) {
          emailSendService.sendEmailAsync(existing);
        } else {
          emailSendService.sendEmail(existing);
        }
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional
  public Email sendByTemplate(String templateCode, Language language, String to, String cc,
      String bcc, Map<String, String> params, boolean async) {
    return new BizTemplate<Email>() {
      EmailTemplate template;

      @Override
      protected void checkParams() {
        // 查找并验证模板
        template = emailTemplateQuery.findAndCheckValid(templateCode, language);

        // 验证收件人不能为空
        assertNotEmpty(to, ProtocolException.of("收件人邮箱不能为空", new Object[]{}));
      }

      @Override
      protected Email process() {
        Email email = toTemplateEmail(template, to, cc, bcc, params);

        // 创建邮件记录
        Email created = self.create(email);

        // 发送邮件
        created = self.send(created.getId(), async);
        return created;
      }
    }.execute();
  }

  @Override
  @Transactional
  public Email retry(Long id) {
    return new BizTemplate<Email>() {
      Email existing;

      @Override
      protected void checkParams() {
        existing = emailQuery.findAndCheck(id);
        if (existing.getStatus() != EmailStatus.FAILED) {
          throw ProtocolException.of("只有失败的邮件可以重试", new Object[]{});
        }
        if (existing.getRetryCount() >= existing.getMaxRetry()) {
          throw ProtocolException.of("已达到最大重试次数", new Object[]{});
        }
      }

      @Override
      protected Email process() {
        existing.setRetryCount(existing.getRetryCount() + 1);
        existing.setStatus(EmailStatus.PENDING);
        existing.setErrorCode(null);
        existing.setErrorMessage(null);
        emailRepo.save(existing);

        emailSendService.sendEmail(existing);
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional
  public Email cancel(Long id) {
    return new BizTemplate<Email>() {
      Email existing;

      @Override
      protected void checkParams() {
        existing = emailQuery.findAndCheck(id);

        if (existing.getStatus() != EmailStatus.PENDING) {
          throw ProtocolException.of("只有待发送状态的邮件可以取消", new Object[]{});
        }
      }

      @Override
      protected Email process() {
        existing.setStatus(EmailStatus.CANCELLED);
        emailRepo.save(existing);
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        emailQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        emailRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  public void update0(Email email) {
    emailRepo.save(email);
  }

  @Override
  protected BaseRepository<Email, Long> getRepository() {
    return emailRepo;
  }
}
