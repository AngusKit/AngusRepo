package cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantCreateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantFindDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatusUpdateVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import java.util.Set;

public class TenantAssembler {

  public static Tenant toCreateDomain(TenantCreateDto dto) {
    Tenant tenant = new Tenant();
    tenant.setName(dto.getName());
    tenant.setCode(dto.getCode());
    tenant.setType(dto.getType());
    tenant.setAccountType(dto.getAccountType());
    tenant.setMainTenantId(getOptTenantId());
    tenant.setAdminName(dto.getAdminName());
    tenant.setAdminEmail(dto.getAdminEmail());
    tenant.setAdminPhone(dto.getAdminPhone());
    tenant.setAddress(dto.getAddress());
    tenant.setStatus(nullSafe(dto.getStatus(), EnabledStatus.ENABLED));
    tenant.setLogo(dto.getLogo());
    tenant.setExpireDate(dto.getExpireDate());
    tenant.setDefaultLanguage(nullSafe(dto.getDefaultLanguage(), Language.fromDefaultValue(
        PrincipalContext.getDefaultLanguage())));
    return tenant;
  }

  public static Tenant toUpdateDomain(Long id, TenantUpdateDto dto) {
    Tenant tenant = new Tenant();
    tenant.setId(id);
    tenant.setName(dto.getName());
    tenant.setCode(dto.getCode());
    tenant.setType(dto.getType());
    tenant.setAccountType(dto.getAccountType());
    tenant.setAdminName(dto.getAdminName());
    tenant.setAdminEmail(dto.getAdminEmail());
    tenant.setAdminPhone(dto.getAdminPhone());
    tenant.setAddress(dto.getAddress());
    //tenant.setStatus(nullSafe(dto.getStatus(), EnabledStatus.ENABLED));
    tenant.setLogo(dto.getLogo());
    tenant.setExpireDate(dto.getExpireDate());
    tenant.setDefaultLanguage(dto.getDefaultLanguage());
    return tenant;
  }

  public static TenantStatusUpdateVo toTenantStatusUpdateVo(Tenant tenant) {
    TenantStatusUpdateVo vo = new TenantStatusUpdateVo();
    vo.setId(tenant.getId());
    vo.setStatus(tenant.getStatus());
    vo.setModifiedDate(tenant.getModifiedDate());
    return vo;
  }

  public static TenantDetailVo toDetailVo(Tenant tenant) {
    TenantDetailVo vo = new TenantDetailVo();
    vo.setId(tenant.getId());
    vo.setName(tenant.getName());
    vo.setCode(tenant.getCode());
    vo.setType(tenant.getType());
    vo.setAccountType(tenant.getAccountType());
    vo.setAdminName(tenant.getAdminName());
    vo.setAdminEmail(tenant.getAdminEmail());
    vo.setAdminPhone(tenant.getAdminPhone());
    vo.setStatus(tenant.getStatus());
    vo.setAddress(tenant.getAddress());
    vo.setExpireDate(tenant.getExpireDate());
    vo.setLogo(tenant.getLogo());
    vo.setDefaultLanguage(tenant.getDefaultLanguage());

    vo.setUserCount(nullSafe(tenant.getUserCount(), 0L));
    vo.setDepartmentCount(nullSafe(tenant.getDepartmentCount(), 0L));
    vo.setSubTenantCount(nullSafe(tenant.getSubTenantCount(), 0L));

    // 设置统计信息
    vo.setCreatedBy(tenant.getCreatedBy());
    vo.setCreatedDate(tenant.getCreatedDate());
    vo.setModifiedBy(tenant.getModifiedBy());
    vo.setModifiedDate(tenant.getModifiedDate());
    return vo;
  }

  public static GenericSpecification<Tenant> getSpecification(TenantFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate", "expireDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name")
        .matchSearchFields("name", "code")
        .build();
    return new GenericSpecification<>(filters);
  }
}
