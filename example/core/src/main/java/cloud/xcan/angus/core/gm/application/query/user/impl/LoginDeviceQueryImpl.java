package cloud.xcan.angus.core.gm.application.query.user.impl;

import cloud.xcan.angus.core.gm.application.query.user.LoginDeviceQuery;
import cloud.xcan.angus.core.gm.domain.user.LoginDevice;
import cloud.xcan.angus.core.gm.domain.user.LoginDeviceRepo;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 登录设备查询服务实现
 */
@Service
public class LoginDeviceQueryImpl implements LoginDeviceQuery {

  @Resource
  private LoginDeviceRepo loginDeviceRepo;

  @Override
  public Page<LoginDevice> findByUserId(Long userId, Pageable pageable) {
    return loginDeviceRepo.findByUserIdOrderByLastActiveAtDesc(userId, pageable);
  }

  @Override
  public LoginDevice findByUserIdAndId(Long userId, Long deviceId) {
    return loginDeviceRepo.findByUserIdAndId(userId, deviceId);
  }

  @Override
  public LoginDevice findByUserIdAndDeviceId(Long userId, String deviceId) {
    return loginDeviceRepo.findByUserIdAndDeviceId(userId, deviceId);
  }
}
