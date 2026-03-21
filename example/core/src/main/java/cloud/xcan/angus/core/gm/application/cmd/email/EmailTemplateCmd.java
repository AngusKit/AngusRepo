package cloud.xcan.angus.core.gm.application.cmd.email;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;

public interface EmailTemplateCmd {

  /**
   * 创建邮件模板
   */
  EmailTemplate create(EmailTemplate template);

  /**
   * 更新邮件模板
   */
  EmailTemplate update(EmailTemplate template);

  /**
   * 更新模板状态
   */
  EmailTemplate updateStatus(Long id, EnabledStatus status);

  /**
   * 删除邮件模板
   */
  void delete(Long id);
}

