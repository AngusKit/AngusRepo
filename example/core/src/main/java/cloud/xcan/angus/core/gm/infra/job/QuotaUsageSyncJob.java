package cloud.xcan.angus.core.gm.infra.job;

import cloud.xcan.angus.api.commonlink.QuotaConstant;
import cloud.xcan.angus.api.commonlink.quota.Quota;
import cloud.xcan.angus.api.commonlink.quota.QuotaRepo;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.enums.AccountType;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.manager.QuotaManager;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.job.JobTemplate;
import cloud.xcan.angus.core.spring.condition.PrivateEditionCondition;
import cloud.xcan.angus.core.utils.PrincipalContextUtils;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 许可配额使用量同步任务
 * <p>
 * 定时同步租户数量（TenantCount）和用户数量（UserCount）到主账号租户的配额。
 * <p>
 * 用户数为同账号下所有租户（主账号+子账号）的用户总数，更新到主账号租户配额。
 * </p>
 */
@Slf4j
@Component
@Conditional(PrivateEditionCondition.class)
public class QuotaUsageSyncJob {

  private static final String LOCK_KEY = "gm:job:QuotaUsageSyncJob";

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private TenantQuery tenantQuery;

  @Resource
  private UserRepo userRepo;

  @Resource
  private QuotaRepo quotaRepo;

  @Resource
  private QuotaManager quotaManager;

  @Resource
  private QuotaUsageSyncJob self;

  /**
   * 每5分钟执行一次许可配额使用量同步
   */
  @Scheduled(fixedDelay = 5 * 60 * 1000, initialDelay = 60000)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 10, TimeUnit.MINUTES, () -> {
      try {
        log.info("开始执行许可配额使用量同步");
        self.syncLicenseQuotaUsage();
        log.info("许可配额使用量同步完成");
      } catch (Exception e) {
        log.error("许可配额使用量同步执行失败", e);
      }
    });
  }

  /**
   * 同步租户数和用户数到主账号租户配额
   */
  @Transactional(rollbackFor = Exception.class)
  protected void syncLicenseQuotaUsage() {
    PrincipalContextUtils.setMultiTenantCtrl(false);

    List<Tenant> mainTenants = tenantQuery.findByAccountType(AccountType.MAIN);
    if (mainTenants.isEmpty()) {
      log.debug("未找到主账号租户，跳过同步");
      return;
    }

    int syncedCount = 0;
    for (Tenant mainTenant : mainTenants) {
      try {
        syncQuotaForMainTenant(mainTenant.getId());
        syncedCount++;
      } catch (Exception e) {
        log.warn("同步主账号租户配额失败：mainTenantId={}", mainTenant.getId(), e);
      }
    }
    log.info("许可配额同步完成，共处理{}个主账号租户", syncedCount);
  }

  /**
   * 同步单个主账号租户的租户数和用户数配额
   *
   * @param mainTenantId 主账号租户ID
   */
  private void syncQuotaForMainTenant(Long mainTenantId) {
    // 获取同账号下所有租户（主账号+子账号）
    List<Tenant> sameAccountTenants = tenantQuery.getSameAccountTenants(mainTenantId);
    List<Long> tenantIds = sameAccountTenants.stream().map(Tenant::getId).toList();

    // 租户数 = 同账号租户数量
    long tenantCount = sameAccountTenants.size();

    // 用户数 = 同账号下所有租户的用户总数
    long userCount = tenantIds.isEmpty() ? 0L : userRepo.countByTenantIdIn(tenantIds);

    // 更新 TenantCount 配额
    updateQuotaUsed(mainTenantId, QuotaConstant.QuotaTenantCount, tenantCount);

    // 更新 UserCount 配额（用户数为同账号租户用户总数，更新到主账号租户）
    updateQuotaUsed(mainTenantId, QuotaConstant.QuotaUserCount, userCount);

    log.debug("主账号租户配额已同步：mainTenantId={}, tenantCount={}, userCount={}",
        mainTenantId, tenantCount, userCount);
  }

  /**
   * 更新配额使用量
   */
  private void updateQuotaUsed(Long mainTenantId, String quotaCode, long used) {
    Quota quota = quotaManager.findTenantQuota(mainTenantId, quotaCode);
    if (quota == null) {
      log.warn("配额不存在，跳过更新：mainTenantId={}, quotaCode={}", mainTenantId, quotaCode);
      return;
    }
    quota.setUsed(used);
    quotaRepo.save(quota);
  }
}
