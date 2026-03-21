package cloud.xcan.angus.core.gm.application.cmd.authentication;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.commonlink.oauthuser.AuthenticationUser;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.gm.domain.user.enums.OAuthProvider;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.PasswordResetDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.RefreshTokenDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.UserSignInDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.UserSignupDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.VerificationCodeSendVo;

/**
 * 认证命令服务接口 负责处理认证相关的写操作：登录、注册、密码重置、token刷新、退出登录等
 */
public interface AuthenticationUserCmd {

  /**
   * 创建OAuth认证用户
   */
  void create0(AuthenticationUser authUser);

  /**
   * 更新OAuth认证用户
   */
  void update0(AuthenticationUser authUser);

  /**
   * 用户登录
   */
  User signIn(UserSignInDto dto);

  /**
   * OAuth第三方登录
   */
  User socialSignIn(OAuthProvider provider, String code, String state);

  /**
   * 用户注册
   */
  User signUp(UserSignupDto dto);

  /**
   * 重置密码
   */
  void resetPassword(PasswordResetDto dto);

  /**
   * 根据旧密码修改密码
   */
  void changePassword(Long id, String oldPassword, String newPassword, String confirmPassword);

  /**
   * 刷新Token
   */
  User refreshToken(RefreshTokenDto dto);

  /**
   * 退出登录
   */
  void logout(String accessToken);

  /**
   * 发送短信验证码
   */
  VerificationCodeSendVo sendSmsCode(String templateCode, Language language, String phone);

  /**
   * 发送邮箱验证码
   */
  VerificationCodeSendVo sendEmailCode(String templateCode, Language language, String email);

  /**
   * 根据ID删除授权用户
   */
  void deleteById(Long id);

  /**
   * 根据租户删除授权用户
   */
  void deleteByTenantId(Long tenantId);

  /**
   * 创建登录失败通知
   */
  void createLoginFailedNotificationInNewTransaction(String username, int passwordErrors);

  /**
   * 在新事务中创建OAuth登录失败通知
   */
  void createOAuthLoginFailedNotificationInNewTransaction(
      String providerName, String openId, String errorMessage);

}
