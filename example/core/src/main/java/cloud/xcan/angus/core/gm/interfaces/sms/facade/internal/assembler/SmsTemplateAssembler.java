package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.sms.SmsTemplate;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTemplateCreateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsTemplateStatusVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsTemplateVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class SmsTemplateAssembler {

  public static SmsTemplate toCreateDomain(SmsTemplateCreateDto dto) {
    SmsTemplate template = new SmsTemplate();
    template.setName(dto.getName());
    template.setCode(dto.getCode());
    template.setLanguage(dto.getLanguage());
    template.setContent(dto.getContent());
    template.setParams(dto.getParams());
    // provider 字段不能为空，如果未提供则使用空字符串
    template.setProvider(dto.getProvider() != null ? dto.getProvider() : "");
    template.setTemplateCode(dto.getTemplateCode());
    template.setSignature(dto.getSignature());
    template.setStatus(EnabledStatus.ENABLED);
    return template;
  }

  public static SmsTemplate toUpdateDomain(Long id, SmsTemplateUpdateDto dto) {
    SmsTemplate template = new SmsTemplate();
    template.setId(id);
    template.setName(dto.getName());
    template.setCode(dto.getCode());
    template.setLanguage(dto.getLanguage());
    template.setContent(dto.getContent());
    template.setParams(dto.getParams());
    // provider 字段不能为空，如果未提供则使用空字符串
    template.setProvider(dto.getProvider() != null ? dto.getProvider() : "");
    template.setTemplateCode(dto.getTemplateCode());
    template.setSignature(dto.getSignature());
    return template;
  }

  public static SmsTemplateStatusVo toSmsTemplateStatusVo(SmsTemplate template) {
    SmsTemplateStatusVo vo = new SmsTemplateStatusVo();
    vo.setId(template.getId());
    vo.setStatus(template.getStatus());
    vo.setModifiedDate(template.getModifiedDate());
    return vo;
  }

  public static SmsTemplateVo toVo(SmsTemplate template) {
    SmsTemplateVo vo = new SmsTemplateVo();
    vo.setId(template.getId());
    vo.setName(template.getName());
    vo.setCode(template.getCode());
    vo.setLanguage(template.getLanguage());
    vo.setContent(template.getContent());
    vo.setParams(template.getParams());
    vo.setStatus(template.getStatus());
    vo.setProvider(template.getProvider());
    vo.setTemplateCode(template.getTemplateCode());
    vo.setSignature(template.getSignature());
    vo.setUsageCount(nullSafe(template.getUsageCount(), 0L));

    // 设置审计字段
    vo.setCreatedBy(template.getCreatedBy());
    vo.setCreatedDate(template.getCreatedDate());
    vo.setModifiedBy(template.getModifiedBy());
    vo.setModifiedDate(template.getModifiedDate());
    return vo;
  }

  public static GenericSpecification<SmsTemplate> getSpecification(
      SmsTemplateFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name", "code")
        .matchSearchFields("name", "code", "content")
        .build();
    return new GenericSpecification<>(filters);
  }
}
