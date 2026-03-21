package cloud.xcan.angus.core.gm.application.cmd.user.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.LoginDeviceCmd;
import cloud.xcan.angus.core.gm.application.query.user.LoginDeviceQuery;
import cloud.xcan.angus.core.gm.domain.user.LoginDevice;
import cloud.xcan.angus.core.gm.domain.user.LoginDeviceRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.ProtocolException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 登录设备命令服务实现
 */
@Service
public class LoginDeviceCmdImpl extends CommCmd<LoginDevice, Long> implements LoginDeviceCmd {

  @Resource
  private LoginDeviceRepo loginDeviceRepo;

  @Resource
  private LoginDeviceQuery loginDeviceQuery;

  @Override
  public void create(LoginDevice device) {
    insert(device);
  }

  @Override
  public void updateOtherDevicesIsCurrentToFalse(Long userId, String deviceId) {
    loginDeviceRepo.updateOtherDevicesIsCurrentToFalse(userId, deviceId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long userId, Long deviceId) {
    new BizTemplate<Void>() {
      LoginDevice existing;

      @Override
      protected void checkParams() {
        // 查找设备
        existing = loginDeviceQuery.findByUserIdAndId(userId, deviceId);
        assertResourceNotFound(existing, deviceId, "设备");
        // 验证不能删除当前登录设备
        if (Boolean.TRUE.equals(existing.getIsCurrent())) {
          throw ProtocolException.of("不能删除当前登录设备");
        }
      }

      @Override
      protected Void process() {
        // 删除设备（TODO: 同时需要使该设备的token失效）
        loginDeviceRepo.deleteById(deviceId);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<LoginDevice, Long> getRepository() {
    return loginDeviceRepo;
  }
}
