package cloud.xcan.angus.core.gm.interfaces.user.facade;

import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.Confirm2FADto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.DevicesQueryDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.Disable2FADto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.Enable2FADto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.Confirm2FAVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.Disable2FAVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.Enable2FAVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.LoginDeviceVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserSecurityVo;
import cloud.xcan.angus.remote.PageResult;

/**
 * 用户安全门面接口
 */
public interface UserSecurityFacade {

  /**
   * 启用双因素认证
   */
  Enable2FAVo enable2FA(Enable2FADto dto);

  /**
   * 确认启用双因素认证
   */
  Confirm2FAVo confirm2FA(Confirm2FADto dto);

  /**
   * 禁用双因素认证
   */
  Disable2FAVo disable2FA(Disable2FADto dto);

  /**
   * 获取安全设置详情
   */
  UserSecurityVo getSecurity();

  /**
   * 获取登录设备列表
   */
  PageResult<LoginDeviceVo> listDevices(DevicesQueryDto dto);

  /**
   * 删除登录设备
   */
  void deleteDevice(Long deviceId);
}
