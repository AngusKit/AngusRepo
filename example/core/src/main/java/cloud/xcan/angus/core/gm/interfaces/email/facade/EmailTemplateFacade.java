package cloud.xcan.angus.core.gm.interfaces.email.facade;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateCreateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTemplateStatusVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTemplateVo;
import cloud.xcan.angus.remote.PageResult;

public interface EmailTemplateFacade {

  /**
   * 创建邮件模板
   */
  EmailTemplateVo createTemplate(EmailTemplateCreateDto dto);

  /**
   * 更新邮件模板
   */
  EmailTemplateVo updateTemplate(Long id, EmailTemplateUpdateDto dto);

  /**
   * 更新邮件模板状态
   */
  EmailTemplateStatusVo updateTemplateStatus(Long id, EnabledStatusUpdateDto dto);

  /**
   * 删除邮件模板
   */
  void deleteTemplate(Long id);

  /**
   * 分页查询邮件模板列表
   */
  PageResult<EmailTemplateVo> listTemplates(EmailTemplateFindDto dto);
}
