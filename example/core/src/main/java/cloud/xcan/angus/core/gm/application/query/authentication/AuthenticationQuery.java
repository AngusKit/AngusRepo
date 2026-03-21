package cloud.xcan.angus.core.gm.application.query.authentication;

import cloud.xcan.angus.api.commonlink.oauthuser.AuthenticationUser;

/**
 * 认证查询服务接口 负责处理认证相关的读操作：验证码验证、用户查询、token解析等
 */
public interface AuthenticationQuery {

  /**
   * 验证图形验证码
   */
  boolean verifyCaptcha(String captchaKey, String captcha);

  /**
   * 从accessToken中解析user
   */
  AuthenticationUser findByToken(String accessToken);

  /**
   * 检查密码长度是否符合设置的最小长度要求
   */
  void checkMinPasswordLengthByConfig(String password);

  /**
   * 检查操作平台登录权限
   */
  void checkOperationPlatformLogin(cloud.xcan.angus.security.model.CustomOAuth2User user);

}
