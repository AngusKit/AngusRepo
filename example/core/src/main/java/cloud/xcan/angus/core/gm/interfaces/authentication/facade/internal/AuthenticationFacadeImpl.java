package cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal;

import static cloud.xcan.angus.api.commonlink.GMConstant.DEFAULT_EMAIL_LANGUAGE;
import static cloud.xcan.angus.api.commonlink.GMConstant.DEFAULT_SMS_LANGUAGE;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserInfo;
import cloud.xcan.angus.core.gm.application.cmd.authentication.AuthenticationUserCmd;
import cloud.xcan.angus.core.gm.application.query.security.SecurityQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserInviteQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.user.UserInvite;
import cloud.xcan.angus.core.gm.infra.authentication.CaptchaService;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.AuthenticationFacade;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.EmailCodeSendDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.InviteCodeVerifyDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.PasswordResetDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.RefreshTokenDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.SmsCodeSendDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.SocialSignInDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.UserSignInDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.UserSignupDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal.assembler.AuthenticationAssembler;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.CaptchaVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.InviteCodeVerifyVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.UserSignInVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.VerificationCodeSendVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.internal.assembler.SecurityAssembler;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.LoginSecurityConfigVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.PasswordPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler;
import cloud.xcan.angus.api.gm.user.vo.UserDetailVo;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacadeImpl implements AuthenticationFacade {

  @Resource
  private AuthenticationUserCmd authenticationCmd;

  @Resource
  private SecurityQuery securityQuery;

  @Resource
  private UserQuery userQuery;

  @Resource
  private UserInviteQuery userInviteQuery;

  @Resource
  private CaptchaService captchaService;

  @Override
  public UserSignInVo signIn(UserSignInDto dto) {
    User user = authenticationCmd.signIn(dto);
    return AuthenticationAssembler.toLoginVo(user.getTokenResult(), user);
  }

  @Override
  public UserSignInVo socialSignIn(SocialSignInDto dto) {
    User user = authenticationCmd.socialSignIn(dto.getProvider(), dto.getCode(), dto.getState());
    return AuthenticationAssembler.toLoginVo(user.getTokenResult(), user);
  }

  @Override
  public UserDetailVo signUp(UserSignupDto dto) {
    User user = authenticationCmd.signUp(dto);
    return UserAssembler.toDetailVo(user);
  }

  @Override
  public void resetPassword(PasswordResetDto dto) {
    authenticationCmd.resetPassword(dto);
  }

  @Override
  public UserSignInVo refreshToken(RefreshTokenDto dto) {
    User user = authenticationCmd.refreshToken(dto);
    return AuthenticationAssembler.toLoginVo(user.getTokenResult(), user);
  }

  @Override
  public void logout(String accessToken) {
    authenticationCmd.logout(accessToken);
  }

  @Override
  public List<UserInfo> findUsersByAccount(String account) {
    List<User> users = userQuery.findAllByAccount(account);
    return users.stream().map(User::toUserInfo).collect(Collectors.toList());
  }

  @Override
  public PasswordPolicyVo getPasswordPolicy() {
    return SecurityAssembler.toPasswordPolicyVo(securityQuery.getPasswordPolicy());
  }

  @Override
  public LoginSecurityConfigVo getLoginSecurityConfig() {
    return SecurityAssembler.toLoginSecurityConfigVo(securityQuery.getLoginSecurityConfig());
  }

  @Override
  public CaptchaVo getCaptcha() {
    return captchaService.generate();
  }

  @Override
  public InviteCodeVerifyVo verifyInviteCode(InviteCodeVerifyDto dto) {
    InviteCodeVerifyVo vo = new InviteCodeVerifyVo();
    try {
      UserInvite userInvite = userInviteQuery.findAndCheck(dto.getInviteCode());
      vo.setValid(true);
      vo.setExpireDate(userInvite.getExpiryDate());
    } catch (Exception e) {
      vo.setValid(false);
    }
    return vo;
  }

  @Override
  public VerificationCodeSendVo sendSmsCode(SmsCodeSendDto dto) {
    return authenticationCmd.sendSmsCode(dto.getTemplateCode(),
        nullSafe(dto.getLanguage(), Language.valueOf(DEFAULT_SMS_LANGUAGE)), dto.getPhone());
  }

  @Override
  public VerificationCodeSendVo sendEmailCode(EmailCodeSendDto dto) {
    return authenticationCmd.sendEmailCode(dto.getTemplateCode(),
        nullSafe(dto.getLanguage(), Language.valueOf(DEFAULT_EMAIL_LANGUAGE)), dto.getEmail());
  }

}
