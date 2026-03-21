package cloud.xcan.angus.core.gm.interfaces.application.facade.internal.assembler;


import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationFindDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationListVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class ApplicationAssembler {

  public static Application toDomain(ApplicationCreateDto dto) {
    Application application = new Application();
    application.setCode(dto.getCode());
    application.setName(dto.getName());
    application.setDisplayName(dto.getDisplayName());
    application.setDescription(dto.getDescription());
    application.setType(dto.getType());
    application.setStatus(nullSafe(dto.getStatus(), EnabledStatus.ENABLED));
    application.setVersion(dto.getVersion());
    application.setClientId(dto.getClientId());
    application.setUrl(dto.getUrl());
    application.setTags(dto.getTags());
    application.setSortOrder(nullSafe(dto.getSortOrder(), 1));
    return application;
  }

  public static Application toDomain(Long id, ApplicationUpdateDto dto) {
    Application application = new Application();
    application.setId(id);
    application.setCode(dto.getCode());
    application.setName(dto.getName());
    application.setDisplayName(dto.getDisplayName());
    application.setDescription(dto.getDescription());
    application.setType(dto.getType());
    application.setVersion(dto.getVersion());
    application.setClientId(dto.getClientId());
    application.setUrl(dto.getUrl());
    application.setTags(dto.getTags());
    application.setSortOrder(dto.getSortOrder());
    return application;
  }

  public static ApplicationDetailVo toDetailVo(Application application) {
    if (application == null) {
      return null;
    }
    ApplicationDetailVo vo = new ApplicationDetailVo();
    vo.setId(application.getId());
    vo.setCode(application.getCode());
    vo.setName(application.getName());
    vo.setDisplayName(application.getDisplayName());
    vo.setDescription(application.getDescription());
    vo.setType(application.getType());
    vo.setSource(application.getSource());
    vo.setStatus(application.getStatus());
    vo.setVersion(application.getVersion());
    vo.setEditionType(application.getEditionType());
    vo.setUrl(application.getUrl());
    vo.setSortOrder(application.getSortOrder());
    vo.setTags(application.getTags());

    // 设置统计字段
    vo.setMenuCount(nullSafe(application.getMenuCount(), 0));
    vo.setRoleCount(nullSafe(application.getRoleCount(), 0));
    vo.setUserCount(nullSafe(application.getUserCount(), 0));

    // 设置审计字段
    vo.setCreatedBy(application.getCreatedBy());
    vo.setCreatedDate(application.getCreatedDate());
    vo.setModifiedBy(application.getModifiedBy());
    vo.setModifiedDate(application.getModifiedDate());
    return vo;
  }

  public static ApplicationListVo toListVo(Application application) {
    ApplicationListVo vo = new ApplicationListVo();
    vo.setId(application.getId());
    vo.setCode(application.getCode());
    vo.setName(application.getName());
    vo.setDisplayName(application.getDisplayName());
    vo.setDescription(application.getDescription());
    vo.setType(application.getType());
    vo.setSource(application.getSource());
    vo.setStatus(application.getStatus());
    vo.setVersion(application.getVersion());
    vo.setEditionType(application.getEditionType());
    vo.setUrl(application.getUrl());
    vo.setSortOrder(application.getSortOrder());
    vo.setTags(application.getTags());

    // 设置审计字段
    vo.setCreatedBy(application.getCreatedBy());
    vo.setCreatedDate(application.getCreatedDate());
    vo.setModifiedBy(application.getModifiedBy());
    vo.setModifiedDate(application.getModifiedDate());
    return vo;
  }

  public static GenericSpecification<Application> getSpecification(ApplicationFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name", "code", "sortOrder")
        .matchSearchFields("name", "code", "displayName", "description")
        .build();
    return new GenericSpecification<>(filters);
  }
}
