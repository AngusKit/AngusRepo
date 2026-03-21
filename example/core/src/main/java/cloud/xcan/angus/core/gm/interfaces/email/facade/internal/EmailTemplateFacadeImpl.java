package cloud.xcan.angus.core.gm.interfaces.email.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailTemplateAssembler.toEmailTemplateStatusVo;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailTemplateCmd;
import cloud.xcan.angus.core.gm.application.query.email.EmailTemplateQuery;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;
import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailTemplateFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateCreateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailTemplateAssembler;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTemplateStatusVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTemplateVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplateFacadeImpl implements EmailTemplateFacade {

  @Resource
  private EmailTemplateCmd emailTemplateCmd;

  @Resource
  private EmailTemplateQuery emailTemplateQuery;

  @Override
  public EmailTemplateVo createTemplate(EmailTemplateCreateDto dto) {
    EmailTemplate template = EmailTemplateAssembler.toCreateDomain(dto);
    EmailTemplate saved = emailTemplateCmd.create(template);
    return EmailTemplateAssembler.toVo(saved);
  }

  @Override
  public EmailTemplateVo updateTemplate(Long id, EmailTemplateUpdateDto dto) {
    EmailTemplate template = EmailTemplateAssembler.toUpdateDomain(id, dto);
    EmailTemplate saved = emailTemplateCmd.update(template);
    emailTemplateQuery.assembleTemplateInfos(List.of(saved));
    return EmailTemplateAssembler.toVo(saved);
  }

  @Override
  public EmailTemplateStatusVo updateTemplateStatus(Long id, EnabledStatusUpdateDto dto) {
    EmailTemplate template = emailTemplateCmd.updateStatus(id, dto.getStatus());
    return toEmailTemplateStatusVo(id, dto, template);
  }

  @Override
  public void deleteTemplate(Long id) {
    emailTemplateCmd.delete(id);
  }

  @Override
  public PageResult<EmailTemplateVo> listTemplates(EmailTemplateFindDto dto) {
    var spec = EmailTemplateAssembler.getSpecification(dto);
    Page<EmailTemplate> page = emailTemplateQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));

    // 批量设置统计信息
    if (page.hasContent()) {
      emailTemplateQuery.assembleTemplateInfos(page.getContent());
    }

    return buildVoPageResult(page, EmailTemplateAssembler::toVo);
  }
}
