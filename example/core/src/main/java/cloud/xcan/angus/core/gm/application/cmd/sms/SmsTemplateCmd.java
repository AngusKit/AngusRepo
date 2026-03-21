package cloud.xcan.angus.core.gm.application.cmd.sms;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.sms.SmsTemplate;

public interface SmsTemplateCmd {

  /**
   * 创建短信模板
   */
  SmsTemplate createTemplate(SmsTemplate template);

  /**
   * 更新短信模板
   */
  SmsTemplate updateTemplate(SmsTemplate template);

  /**
   * 更新短信模板状态
   */
  SmsTemplate updateTemplateStatus(Long id, EnabledStatus status);

  /**
   * 删除短信模板
   */
  void deleteTemplate(Long id);

}
