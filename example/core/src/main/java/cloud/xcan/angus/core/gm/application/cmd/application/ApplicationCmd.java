package cloud.xcan.angus.core.gm.application.cmd.application;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.Application;

public interface ApplicationCmd {

  /**
   * 创建应用
   */
  Application create(Application application);

  /**
   * 更新应用
   */
  Application update(Application application);

  /**
   * 更新应用状态
   */
  Application updateStatus(Long id, EnabledStatus status);

  /**
   * 删除应用
   */
  void delete(Long id);
}
