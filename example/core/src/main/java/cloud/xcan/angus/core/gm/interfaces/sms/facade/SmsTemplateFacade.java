package cloud.xcan.angus.core.gm.interfaces.sms.facade;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTemplateCreateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsTemplateStatusVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsTemplateVo;
import cloud.xcan.angus.remote.PageResult;

public interface SmsTemplateFacade {

  /**
   * 创建短信模板
   */
  SmsTemplateVo createTemplate(SmsTemplateCreateDto dto);

  /**
   * 更新短信模板
   */
  SmsTemplateVo updateTemplate(Long id, SmsTemplateUpdateDto dto);

  /**
   * 更新短信模板状态
   */
  SmsTemplateStatusVo updateTemplateStatus(Long id, EnabledStatusUpdateDto dto);

  /**
   * 删除短信模板
   */
  void deleteTemplate(Long id);

  /**
   * 获取短信模板列表
   */
  PageResult<SmsTemplateVo> listTemplates(SmsTemplateFindDto dto);
}
