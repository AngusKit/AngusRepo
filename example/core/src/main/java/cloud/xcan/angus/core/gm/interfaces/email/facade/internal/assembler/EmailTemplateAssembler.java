package cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateCreateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTemplateStatusVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTemplateVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

/**
 * 邮件模板数据转换器
 */
public class EmailTemplateAssembler {

  public static EmailTemplate toCreateDomain(EmailTemplateCreateDto dto) {
    EmailTemplate template = new EmailTemplate();
    template.setName(dto.getName());
    template.setCode(dto.getCode());
    template.setLanguage(dto.getLanguage());
    template.setSubject(dto.getSubject());
    template.setContent(dto.getContent());
    template.setParams(dto.getParams());
    template.setStatus(EnabledStatus.ENABLED);
    return template;
  }

  public static EmailTemplate toUpdateDomain(Long id, EmailTemplateUpdateDto dto) {
    EmailTemplate template = new EmailTemplate();
    template.setId(id);
    template.setName(dto.getName());
    template.setCode(dto.getCode());
    template.setSubject(dto.getSubject());
    template.setContent(dto.getContent());
    template.setParams(dto.getParams());
    return template;
  }

  public static EmailTemplateStatusVo toEmailTemplateStatusVo(Long id,
      EnabledStatusUpdateDto dto, EmailTemplate template) {
    EmailTemplateStatusVo vo = new EmailTemplateStatusVo();
    vo.setId(id);
    vo.setStatus(dto.getStatus());
    vo.setModifiedDate(template.getModifiedDate());
    return vo;
  }

  public static EmailTemplateVo toVo(EmailTemplate template) {
    EmailTemplateVo vo = new EmailTemplateVo();
    vo.setId(template.getId());
    vo.setName(template.getName());
    vo.setCode(template.getCode());
    vo.setLanguage(template.getLanguage());
    vo.setSubject(template.getSubject());
    vo.setContent(template.getContent());
    vo.setParams(template.getParams());
    vo.setStatus(template.getStatus());
    vo.setIsSystem(template.getIsSystem());

    // 设置统计信息
    vo.setUsageCount(template.getUsageCount());
    vo.setOpenRate(template.getOpenRate());
    vo.setClickRate(template.getClickRate());

    // 设置审计字段
    vo.setCreatedBy(template.getCreatedBy());
    vo.setCreatedDate(template.getCreatedDate());
    vo.setModifiedBy(template.getModifiedBy());
    vo.setModifiedDate(template.getModifiedDate());
    return vo;
  }

  public static GenericSpecification<EmailTemplate> getSpecification(
      EmailTemplateFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name", "code")
        .matchSearchFields("name", "code", "language", "subject")
        .build();
    return new GenericSpecification<>(filters);
  }
}
