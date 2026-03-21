package cloud.xcan.angus.core.gm.application.cmd.user;

import cloud.xcan.angus.core.gm.domain.user.LoginDevice;

/**
 * 登录设备命令服务接口 负责登录设备的写操作
 */
public interface LoginDeviceCmd {

  /**
   * 创建登录设备
   */
  void create(LoginDevice device);

  /**
   * 将其他设备的 isCurrent 字段更新为 false
   */
  void updateOtherDevicesIsCurrentToFalse(Long userId, String deviceId);

  /**
   * 删除登录设备
   */
  void delete(Long userId, Long deviceId);

}
