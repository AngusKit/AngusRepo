package cloud.xcan.angus.core.gm.application.query.tenant.impl;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.QuotaConstant;
import cloud.xcan.angus.api.commonlink.quota.Quota;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.TenantRepo;
import cloud.xcan.angus.api.commonlink.tenant.enums.AccountType;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.manager.TenantManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.quota.QuotaQuery;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.domain.tenant.TenantSearchRepo;
import cloud.xcan.angus.core.gm.infra.utils.CommonUtils;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatsVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantUsageVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class TenantQueryImpl implements TenantQuery {

  @Resource
  private TenantRepo tenantRepo;

  @Resource
  private TenantSearchRepo tenantSearchRepo;

  @Resource
  private UserRepo userRepo;

  @Resource
  private QuotaQuery quotaQuery;

  @Resource
  private TenantManager tenantManager;

  @Override
  public Tenant findAndCheck(Long id) {
    return new BizTemplate<Tenant>(false) {
      @Override
      protected void checkParams() {
        List<Long> allowIds = getTenantIdsBySameAccount();
        if (!allowIds.contains(id)) {
          throw ResourceNotFound.of("租户「{0}」不存在", new Object[]{id});
        }
      }

      @Override
      protected Tenant process() {
        return tenantRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("租户「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Page<Tenant> find(GenericSpecification<Tenant> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Tenant>>(false) {
      @Override
      protected Page<Tenant> process() {
        List<Long> allowIds = getTenantIdsBySameAccount();
        spec.getCriteria().add(SearchCriteria.in("id", allowIds));

        return fullTextSearch
            ? tenantSearchRepo.find(spec.getCriteria(), pageable, Tenant.class, match)
            : tenantRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public TenantStatsVo getStats() {
    return new BizTemplate<TenantStatsVo>(false) {
      @Override
      protected TenantStatsVo process() {
        TenantStatsVo stats = new TenantStatsVo();

        List<Tenant> tenants = getSameAccountTenants();
        long totalTenants = tenants.size();
        stats.setTotalTenants(totalTenants);

        long enabledTenants = tenants.stream()
            .filter(t -> t.getStatus() == EnabledStatus.ENABLED).count();
        stats.setEnabledTenants(enabledTenants);

        long disabledTenants = tenants.stream()
            .filter(t -> t.getStatus() == EnabledStatus.DISABLED).count();
        stats.setDisabledTenants(disabledTenants);

        List<Long> tenantIds = tenants.stream().map(Tenant::getId).toList();
        long totalUsers = userRepo.countByTenantIdIn(tenantIds);
        stats.setTotalUsers(totalUsers);

        // 获取本月新增租户数
        LocalDateTime firstDayOfMonth = LocalDateTime.now()
            .with(TemporalAdjusters.firstDayOfMonth())
            .withHour(0).withMinute(0).withSecond(0).withNano(0);
        long newTenantsThisMonth = tenants.stream()
            .filter(t -> t.getCreatedDate().isAfter(firstDayOfMonth)).count();
        stats.setNewTenantsThisMonth(newTenantsThisMonth);

        // 计算增长率
        long existingTenants = totalTenants - newTenantsThisMonth;
        if (existingTenants > 0) {
          double growthRate = (newTenantsThisMonth * 100.0) / existingTenants;
          stats.setGrowthRate(Math.round(growthRate * 10.0) / 10.0);
        } else {
          stats.setGrowthRate(0.0);
        }
        return stats;
      }
    }.execute();
  }

  @Override
  public TenantUsageVo getUsage(Long id) {
    return new BizTemplate<TenantUsageVo>(false) {
      @Override
      protected TenantUsageVo process() {
        TenantUsageVo vo = new TenantUsageVo();

        // 获取配额信息（使用SQL查询，性能更好）
        Tenant mainAccountTenant = getMainTenantOfSameAccount(id);
        List<Quota> quotas = quotaQuery.findByMainTenantId(mainAccountTenant.getId());
        Map<String, Quota> quotaMap = quotas.stream()
            .collect(Collectors.toMap(Quota::getCode, q -> q));

        // 用户使用情况
        TenantUsageVo.UsageItemVo usersUsage = new TenantUsageVo.UsageItemVo();
        Quota userQuota = quotaMap.get(QuotaConstant.QuotaUserCount);
        assert userQuota != null;
        usersUsage.setCurrent(userQuota.getUsed());
        long maxUsers = userQuota.getLimit() > 0 ? userQuota.getLimit() : 0L;
        usersUsage.setMax(maxUsers);
        if (maxUsers > 0) {
          usersUsage.setUsage((double) userQuota.getUsed() / maxUsers * 100);
        } else {
          usersUsage.setUsage(0.0);
        }
        vo.setUsers(usersUsage);

        // 组使用情况
        TenantUsageVo.UsageItemVo groupsUsage = new TenantUsageVo.UsageItemVo();
        Quota groupQuota = quotaMap.get(QuotaConstant.QuotaGroupCount);
        assert groupQuota != null;
        groupsUsage.setCurrent(groupQuota.getUsed());
        long maxGroups = groupQuota.getLimit() > 0 ? groupQuota.getLimit() : 0L;
        groupsUsage.setMax(maxGroups);
        if (maxGroups > 0) {
          groupsUsage.setUsage((double) groupQuota.getUsed() / maxGroups * 100);
        } else {
          groupsUsage.setUsage(0.0);
        }
        vo.setGroups(groupsUsage);

        // 部门使用情况
        TenantUsageVo.UsageItemVo departmentsUsage = new TenantUsageVo.UsageItemVo();
        Quota departmentQuota = quotaMap.get(QuotaConstant.QuotaDepartmentCount);
        assert departmentQuota != null;
        departmentsUsage.setCurrent(departmentQuota.getUsed());
        long maxDepartments = departmentQuota.getLimit() > 0 ? departmentQuota.getLimit() : 0L;
        departmentsUsage.setMax(maxDepartments);
        if (maxDepartments > 0) {
          departmentsUsage.setUsage((double) departmentQuota.getUsed() / maxDepartments * 100);
        } else {
          departmentsUsage.setUsage(0.0);
        }
        vo.setDepartments(departmentsUsage);

        // 存储使用情况
        TenantUsageVo.StorageUsageItemVo storageUsage = new TenantUsageVo.StorageUsageItemVo();
        Quota storageQuota = quotaMap.get(QuotaConstant.QuotaStorageSpace);
        assert storageQuota != null;
        long currentStorageBytes = storageQuota.getUsed();
        long maxStorageBytes = storageQuota.getLimit() > 0 ? storageQuota.getLimit() : 0L;
        // 存储配额单位可能是GB，需要转换为字节
        if ("GB".equals(storageQuota.getUnit())) {
          maxStorageBytes = maxStorageBytes * 1024L * 1024L * 1024L;
        }
        storageUsage.setCurrent(CommonUtils.formatFileSize(currentStorageBytes));
        storageUsage.setMax(CommonUtils.formatFileSize(maxStorageBytes));
        if (maxStorageBytes > 0) {
          storageUsage.setUsage((double) currentStorageBytes / maxStorageBytes * 100);
        } else {
          storageUsage.setUsage(0.0);
        }
        vo.setStorage(storageUsage);

        // API调用情况（统计本月调用次数，使用SQL COUNT查询，性能更好）
        TenantUsageVo.ApiUsageItemVo apiCallsUsage = new TenantUsageVo.ApiUsageItemVo();
        Quota apiCallsQuota = quotaMap.get(QuotaConstant.QuotaApiCalls);
        assert apiCallsQuota != null;
        apiCallsUsage.setCurrent(apiCallsQuota.getUsed());
        long maxApiCalls = apiCallsQuota.getLimit() > 0 ? apiCallsQuota.getLimit() : 0L;
        apiCallsUsage.setMax(maxApiCalls);
        if (maxApiCalls > 0) {
          apiCallsUsage.setUsage((double) apiCallsQuota.getUsed() / maxApiCalls * 100);
        } else {
          apiCallsUsage.setUsage(0.0);
        }
        vo.setApiCalls(apiCallsUsage);
        return vo;
      }
    }.execute();
  }

  @Override
  public List<Tenant> findByAccountType(AccountType accountType) {
    return tenantRepo.findByAccountType(accountType);
  }

  @Override
  public List<Tenant> getSameAccountTenants() {
    return tenantManager.getSameAccountTenants();
  }

  @Override
  public List<Long> getTenantIdsBySameAccount() {
    return tenantManager.getTenantIdsBySameAccount();
  }

  @Override
  public Tenant getMainTenantOfSameAccount(Long tenantId) {
    return tenantManager.getMainTenantOfSameAccount(tenantId);
  }

  @Override
  public List<Tenant> getSameAccountTenants(Long tenantId) {
    return tenantManager.getSameAccountTenants(tenantId);
  }

  @Override
  public List<Long> getTenantIdsBySameAccount(Long tenantId) {
    return tenantManager.getTenantIdsBySameAccount(tenantId);
  }

  @Override
  public Long getMainTenantId(Long currentTenantId) {
    return tenantManager.getMainTenantId(currentTenantId);
  }

  @Override
  public boolean isMainTenant(Long tenantId) {
    return tenantManager.isMainTenant(tenantId);
  }

  @Override
  public void checkMultitenancyPermission() {
    tenantManager.checkMultitenancyPermission();
  }

  @Override
  public void checkValidStatus(String tenantId) {
    tenantManager.checkValidStatus(tenantId);
  }
}
