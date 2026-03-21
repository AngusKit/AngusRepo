package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsTemplateCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsTemplateQuery;
import cloud.xcan.angus.core.gm.domain.sms.SmsTemplate;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsTemplateFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTemplateCreateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsTemplateAssembler;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsTemplateStatusVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsTemplateVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class SmsTemplateFacadeImpl implements SmsTemplateFacade {

  @Resource
  private SmsTemplateCmd smsTemplateCmd;

  @Resource
  private SmsTemplateQuery smsTemplateQuery;

  @NameJoin
  @Override
  public SmsTemplateVo createTemplate(SmsTemplateCreateDto dto) {
    SmsTemplate template = SmsTemplateAssembler.toCreateDomain(dto);
    SmsTemplate saved = smsTemplateCmd.createTemplate(template);
    return SmsTemplateAssembler.toVo(saved);
  }

  @NameJoin
  @Override
  public SmsTemplateVo updateTemplate(Long id, SmsTemplateUpdateDto dto) {
    SmsTemplate template = SmsTemplateAssembler.toUpdateDomain(id, dto);
    SmsTemplate saved = smsTemplateCmd.updateTemplate(template);
    return SmsTemplateAssembler.toVo(saved);
  }

  @Override
  public SmsTemplateStatusVo updateTemplateStatus(Long id, EnabledStatusUpdateDto dto) {
    SmsTemplate template = smsTemplateCmd.updateTemplateStatus(id, dto.getStatus());
    return SmsTemplateAssembler.toSmsTemplateStatusVo(template);
  }

  @Override
  public void deleteTemplate(Long id) {
    smsTemplateCmd.deleteTemplate(id);
  }

  @NameJoin
  @Override
  public PageResult<SmsTemplateVo> listTemplates(SmsTemplateFindDto dto) {
    GenericSpecification<SmsTemplate> spec = SmsTemplateAssembler.getSpecification(dto);
    Page<SmsTemplate> page = smsTemplateQuery.findTemplates(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, SmsTemplateAssembler::toVo);
  }
}
