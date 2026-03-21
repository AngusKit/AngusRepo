package cloud.xcan.angus.core.gm.application.cmd.quota;

import cloud.xcan.angus.api.commonlink.quota.Quota;
import java.util.List;

/**
 * 资源配额命令服务接口
 */
public interface QuotaCmd {

  /**
   * 更新资源配额
   */
  Quota update(Quota quota);

  /**
   * 批量更新配额限额
   */
  List<Quota> batchUpdateLimits(List<String> codes, List<Long> limits);

  /**
   * 更新配额状态
   */
  Quota updateStatus(String code, Boolean enabled);

  /**
   * 根据租户ID和所有模版配额初始化租户配额
   *
   * @param tenantId 租户ID
   * @return 创建的配额列表
   */
  List<Quota> initTenantQuotasFromTemplates(Long tenantId);

}
