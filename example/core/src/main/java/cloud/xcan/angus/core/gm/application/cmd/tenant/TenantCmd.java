package cloud.xcan.angus.core.gm.application.cmd.tenant;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;

public interface TenantCmd {

  /**
   * 创建租户
   */
  Tenant create(Tenant tenant);

  /**
   * 更新租户
   */
  Tenant update(Tenant tenant);

  /**
   * 更新租户状态
   */
  Tenant updateStatus(Long id, EnabledStatus status);

  /**
   * 删除租户
   */
  void delete(Long id);

}
