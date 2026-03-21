package cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.user.LoginDevice;
import cloud.xcan.angus.core.gm.domain.user.UserSecurity;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.Confirm2FAVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.Disable2FAVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.Enable2FAVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.LoginDeviceVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserSecurityVo;
import java.time.LocalDateTime;

/**
 * 用户安全数据组装器
 */
public class UserSecurityAssembler {

  public static Enable2FAVo toEnable2FAVo(UserSecurity security, String qrCode) {
    Enable2FAVo vo = new Enable2FAVo();
    vo.setQrCode(qrCode);
    vo.setSecret(security.getTwoFactorSecret());
    vo.setBackupCodes(security.getBackupCodes());
    return vo;
  }

  public static Confirm2FAVo toConfirm2FAVo(UserSecurity security) {
    Confirm2FAVo vo = new Confirm2FAVo();
    vo.setUserId(security.getUserId());
    vo.setTwoFactorEnabled(security.getTwoFactorEnabled());
    vo.setEnabledAt(LocalDateTime.now());
    return vo;
  }

  public static Disable2FAVo toDisable2FAVo(UserSecurity security) {
    Disable2FAVo vo = new Disable2FAVo();
    vo.setUserId(security.getUserId());
    vo.setTwoFactorEnabled(security.getTwoFactorEnabled());
    vo.setDisabledAt(LocalDateTime.now());
    return vo;
  }

  public static UserSecurityVo toVo(UserSecurity security) {
    if (security == null) {
      return null;
    }
    UserSecurityVo vo = new UserSecurityVo();
    vo.setUserId(security.getUserId());
    vo.setTwoFactorEnabled(security.getTwoFactorEnabled());
    vo.setPasswordLastChanged(security.getPasswordLastChanged());
    vo.setPasswordStrength(security.getPasswordStrength());
    vo.setHasBackupCodes(security.getBackupCodes() != null && !security.getBackupCodes().isEmpty());
    vo.setBackupCodesRemaining(
        security.getBackupCodes() != null ? security.getBackupCodes().size() : 0);
    vo.setLastLoginAt(security.getLastLoginAt());
    vo.setLastLoginIp(security.getLastLoginIp());
    vo.setLastLoginLocation(security.getLastLoginLocation());
    vo.setLastLoginDevice(security.getLastLoginDevice());
    return vo;
  }

  public static LoginDeviceVo toDeviceVo(LoginDevice device) {
    LoginDeviceVo vo = new LoginDeviceVo();
    vo.setId(device.getId());
    vo.setUserId(device.getUserId());
    vo.setDeviceName(device.getDeviceName());
    vo.setDeviceType(device.getDeviceType());
    vo.setBrowser(device.getBrowser());
    vo.setBrowserVersion(device.getBrowserVersion());
    vo.setOs(device.getOs());
    vo.setOsVersion(device.getOsVersion());
    vo.setIpAddress(device.getIpAddress());
    vo.setLocation(device.getLocation());
    vo.setIsCurrent(device.getIsCurrent());
    vo.setLastActiveAt(device.getLastActiveAt());
    return vo;
  }

}
