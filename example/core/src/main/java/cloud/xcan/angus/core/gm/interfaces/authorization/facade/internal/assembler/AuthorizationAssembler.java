package cloud.xcan.angus.core.gm.interfaces.authorization.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.authorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationFindDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationListVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationRoleVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.List;
import java.util.Set;

public class AuthorizationAssembler {

  public static Authorization toCreateDomain(AuthorizationCreateDto dto) {
    Authorization authorization = new Authorization();
    authorization.setSubjectType(dto.getSubjectType());
    authorization.setSubjectId(dto.getSubjectId());
    authorization.setRoleIds(dto.getRoleIds());
    authorization.setStatus(EnabledStatus.ENABLED);
    authorization.setValidFrom(dto.getValidFrom());
    authorization.setValidTo(dto.getValidTo());
    authorization.setDescription(dto.getDescription());
    return authorization;
  }

  public static Authorization toCreateDomain(AuthorizationSubjectType subjectType, Long subjectId,
      List<Long> roleIds, String description) {
    Authorization authorization = new Authorization();
    authorization.setSubjectType(subjectType);
    authorization.setSubjectId(subjectId);
    authorization.setRoleIds(roleIds);
    authorization.setStatus(EnabledStatus.ENABLED);
    authorization.setValidFrom(null);
    authorization.setValidTo(null);
    authorization.setDescription(description);
    return authorization;
  }

  public static Authorization toUpdateDomain(Long id, AuthorizationUpdateDto dto) {
    Authorization authorization = new Authorization();
    authorization.setId(id);
    authorization.setRoleIds(dto.getRoleIds());
    authorization.setValidFrom(dto.getValidFrom());
    authorization.setValidTo(dto.getValidTo());
    authorization.setDescription(dto.getDescription());
    return authorization;
  }

  public static AuthorizationRoleVo toAuthorizationRoleAddVo(Long id,
      Authorization authorization) {
    AuthorizationRoleVo result = new AuthorizationRoleVo();
    result.setAuthorizationId(id);
    result.setModifiedDate(authorization.getModifiedDate());
    result.setRoles(authorization.getRoleInfos());
    return result;
  }

  public static AuthorizationDetailVo toDetailVo(Authorization authorization) {
    if (authorization == null) {
      return null;
    }
    AuthorizationDetailVo vo = new AuthorizationDetailVo();
    vo.setId(authorization.getId());
    vo.setSubjectType(authorization.getSubjectType());
    vo.setSubjectId(authorization.getSubjectId());
    vo.setSubjectName(authorization.getSubjectName());
    vo.setSubjectUserCount(authorization.getSubjectUserCount());
    vo.setStatus(authorization.getStatus());
    vo.setOpened(authorization.getOpened());
    vo.setValidFrom(authorization.getValidFrom());
    vo.setValidTo(authorization.getValidTo());
    vo.setDescription(authorization.getDescription());

    // 设置关联字段
    vo.setRoles(authorization.getRoleInfos());

    // 设置审计字段
    vo.setTenantId(authorization.getTenantId());
    vo.setCreatedBy(authorization.getCreatedBy());
    vo.setCreatedDate(authorization.getCreatedDate());
    vo.setModifiedBy(authorization.getModifiedBy());
    vo.setModifiedDate(authorization.getModifiedDate());
    return vo;
  }

  public static AuthorizationListVo toListVo(Authorization authorization) {
    if (authorization == null) {
      return null;
    }
    AuthorizationListVo vo = new AuthorizationListVo();
    vo.setId(authorization.getId());
    vo.setSubjectType(authorization.getSubjectType());
    vo.setSubjectId(authorization.getSubjectId());
    vo.setSubjectName(authorization.getSubjectName());
    vo.setSubjectUserCount(authorization.getSubjectUserCount());
    vo.setStatus(authorization.getStatus());
    vo.setOpened(authorization.getOpened());
    vo.setValidFrom(authorization.getValidFrom());
    vo.setValidTo(authorization.getValidTo());
    vo.setDescription(authorization.getDescription());

    // 设置关联字段
    vo.setRoles(authorization.getRoleInfos());

    // 设置授权人数
    vo.setSubjectUserCount(authorization.getSubjectUserCount());

    // 设置审计字段
    vo.setTenantId(authorization.getTenantId());
    vo.setCreatedBy(authorization.getCreatedBy());
    vo.setCreatedDate(authorization.getCreatedDate());
    vo.setModifiedBy(authorization.getModifiedBy());
    vo.setModifiedDate(authorization.getModifiedDate());
    return vo;
  }

  public static GenericSpecification<Authorization> getSpecification(AuthorizationFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "subjectType", "subjectId", "status", "createdDate", "modifiedDate")
        .matchSearchFields("subjectName", "description")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<Authorization> getSpecificationByType(
      AuthorizationFindDto dto, AuthorizationSubjectType subjectType) {
    dto.setSubjectType(subjectType);
    return getSpecification(dto);
  }
}
