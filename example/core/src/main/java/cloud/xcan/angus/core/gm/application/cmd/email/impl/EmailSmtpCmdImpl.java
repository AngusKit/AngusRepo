package cloud.xcan.angus.core.gm.application.cmd.email.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.PermissionCheck;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailSmtpCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.domain.email.EmailSmtp;
import cloud.xcan.angus.core.gm.domain.email.EmailSmtpRepo;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailSmtpCmdImpl extends CommCmd<EmailSmtp, Long> implements EmailSmtpCmd {

  @Resource
  private EmailSmtpRepo emailSmtpRepo;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public EmailSmtp save(EmailSmtp smtp) {
    return new BizTemplate<EmailSmtp>(false) {
      EmailSmtp existingDefault;

      @Override
      protected EmailSmtp process() {
        PermissionCheck.checkCloudTenantSecurity();
        existingDefault = emailSmtpRepo.findByIsDefaultTrue();

        if (Boolean.TRUE.equals(smtp.getIsDefault())) {
          if (existingDefault != null && !existingDefault.getHost().equals(smtp.getHost())) {
            existingDefault.setIsDefault(false);
            emailSmtpRepo.save(existingDefault);
          }
        }

        if (existingDefault == null) {
          // New SMTP config, set as default if no default exists
          smtp.setIsDefault(true);
          insert(smtp);

          // 记录操作日志
          String smtpInfo = smtp.getHost() + ":" + smtp.getPort();
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.CREATE,
              ResourceType.CONFIG,
              smtp.getId(),
              smtpInfo,
              OperationMessage.EMAIL_SMTP_CREATE_DETAILS,
              new Object[]{smtpInfo}
          );
          return smtp;
        } else {
          // Update existing
          update(smtp, existingDefault);

          // 记录操作日志
          String smtpInfo = existingDefault.getHost() + ":" + existingDefault.getPort();
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.CONFIG,
              existingDefault.getId(),
              smtpInfo,
              OperationMessage.EMAIL_SMTP_UPDATE_DETAILS,
              new Object[]{smtpInfo}
          );
          return existingDefault;
        }
      }
    }.execute();
  }

  @Override
  protected BaseRepository<EmailSmtp, Long> getRepository() {
    return emailSmtpRepo;
  }
}

