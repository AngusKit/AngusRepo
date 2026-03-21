package cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantCmd;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentQuery;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.TenantFacade;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantCreateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantFindDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler.TenantAssembler;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatsVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatusUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantUsageVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class TenantFacadeImpl implements TenantFacade {

  @Resource
  private TenantCmd tenantCmd;

  @Resource
  private TenantQuery tenantQuery;

  @Resource
  private UserQuery userQuery;

  @Resource
  private DepartmentQuery departmentQuery;

  @NameJoin
  @Override
  public TenantDetailVo create(TenantCreateDto dto) {
    Tenant tenant = TenantAssembler.toCreateDomain(dto);
    Tenant saved = tenantCmd.create(tenant);
    return TenantAssembler.toDetailVo(saved);
  }

  @NameJoin
  @Override
  public TenantDetailVo update(Long id, TenantUpdateDto dto) {
    Tenant tenant = TenantAssembler.toUpdateDomain(id, dto);
    Tenant saved = tenantCmd.update(tenant);
    return TenantAssembler.toDetailVo(saved);
  }

  @Override
  public TenantStatusUpdateVo updateStatus(Long id, EnabledStatusUpdateDto dto) {
    Tenant tenant = tenantCmd.updateStatus(id, dto.getStatus());
    return TenantAssembler.toTenantStatusUpdateVo(tenant);
  }

  @Override
  public void delete(Long id) {
    tenantCmd.delete(id);
  }

  @NameJoin
  @Override
  public TenantDetailVo getDetail(Long id) {
    Tenant tenant = tenantQuery.findAndCheck(id);
    // 设置关联统计数据
    assembleCountStats(tenant);
    return TenantAssembler.toDetailVo(tenant);
  }

  @NameJoin
  @Override
  public PageResult<TenantDetailVo> list(TenantFindDto dto) {
    GenericSpecification<Tenant> spec = TenantAssembler.getSpecification(dto);
    Page<Tenant> page = tenantQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    // 设置关联统计数据
    if (page.hasContent()) {
      for (Tenant tenant : page.getContent()) {
        assembleCountStats(tenant);
      }
    }
    return buildVoPageResult(page, TenantAssembler::toDetailVo);
  }

  @NameJoin
  @Override
  public List<TenantDetailVo> getSameAccountTenants() {
    List<Tenant> tenants = tenantQuery.getSameAccountTenants();
    for (Tenant tenant : tenants) {
      assembleCountStats(tenant);
    }
    return tenants.stream()
        .map(TenantAssembler::toDetailVo)
        .collect(Collectors.toList());
  }

  @Override
  public TenantStatsVo getStats() {
    return tenantQuery.getStats();
  }

  @Override
  public TenantUsageVo getUsage(Long id) {
    return tenantQuery.getUsage(id);
  }

  private void assembleCountStats(Tenant tenant) {
    Long userCount = userQuery.countByTenantId(tenant.getId());
    tenant.setUserCount(userCount);
    Long departmentCount = departmentQuery.countByTenantId(tenant.getId());
    tenant.setDepartmentCount(departmentCount);
    if (tenant.getAccountType().isMainAccount()) {
      int subTenantCount = tenantQuery.getTenantIdsBySameAccount(tenant.getId()).size() - 1;
      tenant.setSubTenantCount((long) subTenantCount);
    }
  }

}
