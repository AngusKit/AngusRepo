package cloud.xcan.angus.core.gm.application.cmd.email;

import cloud.xcan.angus.core.gm.domain.email.EmailSmtp;

public interface EmailSmtpCmd {

  /**
   * 创建或更新SMTP配置
   */
  EmailSmtp save(EmailSmtp smtp);
}

