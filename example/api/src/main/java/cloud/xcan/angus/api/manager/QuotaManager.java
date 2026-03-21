package cloud.xcan.angus.api.manager;

import cloud.xcan.angus.api.commonlink.quota.Quota;

public interface QuotaManager extends ManagerMessage {

  /**
   * 查找租户配额
   * <p>
   * 如果配额不存在，会根据模板配额自动创建。
   * </p>
   */
  Quota findTenantQuota(Long mainTenantId, String quotaCode);

  /**
   * 检查租户配额
   */
  void checkTenantQuota(Long mainTenantId, String quotaCode, Long currentUsedCount);

  /**
   * 根据总使用量检查租户配额
   * <p>
   * 使用配额对象中存储的已使用量（used字段）进行检查。
   * </p>
   */
  void checkTenantQuotaByTotalUsage(Long mainTenantId, String quotaCode);

  /**
   * 根据总使用量检查租户配额
   * <p>
   * 使用传入的总使用量参数进行检查。
   * </p>
   */
  void checkTenantQuotaByTotalUsage(Long mainTenantId, String quotaCode, Long totalUsage);

  /**
   * 增加租户配额
   */
  void increaseTenantQuota(Long mainTenantId, String quotaCode, Long amount);

  /**
   * 减少租户配额
   */
  void decreaseTenantQuota(Long mainTenantId, String quotaCode, Long amount);

  /**
   * 重置租户配额
   */
  void resetTenantQuota(Long mainTenantId, String quotaCode);
}
