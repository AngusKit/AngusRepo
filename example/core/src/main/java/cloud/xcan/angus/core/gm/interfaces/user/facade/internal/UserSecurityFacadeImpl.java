package cloud.xcan.angus.core.gm.interfaces.user.facade.internal;

import static cloud.xcan.angus.api.commonlink.GMConstant.GM_APP_CODE;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.biz.MessageJoin;
import cloud.xcan.angus.core.gm.application.cmd.user.LoginDeviceCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserSecurityCmd;
import cloud.xcan.angus.core.gm.application.query.user.LoginDeviceQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserSecurityQuery;
import cloud.xcan.angus.core.gm.domain.user.LoginDevice;
import cloud.xcan.angus.core.gm.domain.user.UserSecurity;
import cloud.xcan.angus.core.gm.infra.user.TotpService;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserSecurityFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.Confirm2FADto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.DevicesQueryDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.Disable2FADto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.Enable2FADto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserSecurityAssembler;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.Confirm2FAVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.Disable2FAVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.Enable2FAVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.LoginDeviceVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserSecurityVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * 用户安全门面实现
 */
@Component
public class UserSecurityFacadeImpl implements UserSecurityFacade {

  @Resource
  private UserSecurityCmd userSecurityCmd;

  @Resource
  private UserSecurityQuery userSecurityQuery;

  @Resource
  private LoginDeviceCmd loginDeviceCmd;

  @Resource
  private LoginDeviceQuery loginDeviceQuery;

  @Resource
  private UserQuery userQuery;

  @Resource
  private TotpService totpService;

  @Override
  @MessageJoin
  public Enable2FAVo enable2FA(Enable2FADto dto) {
    Long userId = getUserId();
    UserSecurity security = userSecurityCmd.enable2FA(userId, dto.getPassword());
    User user = userQuery.findAndCheck(userId);
    String qrCode = totpService.generateQRCode(
        security.getTwoFactorSecret(), user.getEmail(), GM_APP_CODE);
    return UserSecurityAssembler.toEnable2FAVo(security, qrCode);
  }

  @Override
  @MessageJoin
  public Confirm2FAVo confirm2FA(Confirm2FADto dto) {
    UserSecurity security = userSecurityCmd.confirm2FA(getUserId(), dto.getCode());
    return UserSecurityAssembler.toConfirm2FAVo(security);
  }

  @Override
  @MessageJoin
  public Disable2FAVo disable2FA(Disable2FADto dto) {
    UserSecurity security = userSecurityCmd.disable2FA(
        getUserId(), dto.getPassword(), dto.getCode());
    return UserSecurityAssembler.toDisable2FAVo(security);
  }

  @Override
  @MessageJoin
  public UserSecurityVo getSecurity() {
    UserSecurity security = userSecurityQuery.findByUserId(getUserId());
    return UserSecurityAssembler.toVo(security);
  }

  @Override
  @MessageJoin
  public PageResult<LoginDeviceVo> listDevices(DevicesQueryDto dto) {
    Page<LoginDevice> devices = loginDeviceQuery.findByUserId(getUserId(), dto.tranPage());
    return buildVoPageResult(devices, UserSecurityAssembler::toDeviceVo);
  }

  @Override
  public void deleteDevice(Long deviceId) {
    loginDeviceCmd.delete(getUserId(), deviceId);
  }
}
