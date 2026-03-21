package cloud.xcan.angus.core.gm.interfaces.authentication.facade;

import cloud.xcan.angus.api.commonlink.user.UserInfo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.EmailCodeSendDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.InviteCodeVerifyDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.PasswordResetDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.RefreshTokenDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.SmsCodeSendDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.SocialSignInDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.UserSignInDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.UserSignupDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.CaptchaVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.InviteCodeVerifyVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.UserSignInVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.VerificationCodeSendVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.LoginSecurityConfigVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.PasswordPolicyVo;
import cloud.xcan.angus.api.gm.user.vo.UserDetailVo;
import java.util.List;

public interface AuthenticationFacade {

  UserSignInVo signIn(UserSignInDto dto);

  UserSignInVo socialSignIn(SocialSignInDto dto);

  UserDetailVo signUp(UserSignupDto dto);

  void resetPassword(PasswordResetDto dto);

  UserSignInVo refreshToken(RefreshTokenDto dto);

  void logout(String accessToken);

  List<UserInfo> findUsersByAccount(String account);

  PasswordPolicyVo getPasswordPolicy();

  LoginSecurityConfigVo getLoginSecurityConfig();

  CaptchaVo getCaptcha();

  InviteCodeVerifyVo verifyInviteCode(InviteCodeVerifyDto dto);

  VerificationCodeSendVo sendSmsCode(SmsCodeSendDto dto);

  VerificationCodeSendVo sendEmailCode(EmailCodeSendDto dto);

}
