package cloud.xcan.angus.core.gm.application.query.user;

import cloud.xcan.angus.core.gm.domain.user.LoginDevice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 登录设备查询服务接口 负责登录设备的读操作
 */
public interface LoginDeviceQuery {

  /**
   * 根据用户ID分页查询登录设备
   */
  Page<LoginDevice> findByUserId(Long userId, Pageable pageable);

  /**
   * 根据用户ID和设备ID查找设备
   */
  LoginDevice findByUserIdAndId(Long userId, Long deviceId);

  /**
   * 根据用户ID和设备唯一标识查找设备
   */
  LoginDevice findByUserIdAndDeviceId(Long userId, String deviceId);
}
