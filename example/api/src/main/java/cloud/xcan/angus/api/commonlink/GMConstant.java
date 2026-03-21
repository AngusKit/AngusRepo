package cloud.xcan.angus.api.commonlink;

import static cloud.xcan.angus.spec.experimental.BizConstant.XCAN_2P_PLATFORM_CODE;
import static cloud.xcan.angus.spec.experimental.BizConstant.XCAN_TENANT_PLATFORM_CODE;

public interface GMConstant {

  /**
   * 租户端OAuth2认证客户端ID
   */
  String TENANT_OAUTH2_CLIENT_ID = "xcan_tp";

  String GM_APP_CODE = "AngusGM";

  String SYS_ADMIN_ROLE_NAME = "ROEL_SYS_ADMIN";

  int DEFAULT_TOKEN_EXPIRE_SECOND = 30 * 24 * 60 * 60;

  String USER_INFO_ENDPOINT = "/api/v1/auth/user";
  String TOKEN_REVOKE_ENDPOINT = "/api/v1/auth/token";

  String SWAGGER_API_URL = "/v3/api-docs/user";
  String SWAGGER_PUB_API_URL = "/v3/api-docs/public";
  String SWAGGER_INNER_API_URL = "/v3/api-docs/inner";
  String SWAGGER_OPEN_API_TO_PRIVATE_URL = "/v3/api-docs/openapi2p";

  //int LINK_SECRET_LENGTH = 40;
  int LINK_SECRET_VALID_SECOND = 5 * 60;

  String CACHE_SMS_CHECK_SECRET_PREFIX = "oauth2:signin:checkSms:%s:%s";
  String CACHE_EMAIL_CHECK_SECRET_PREFIX = "oauth2:signin:checkEmail:%s:%s";
  //String CACHE_USER_SOCIAL_CHECK_SECRET_PREFIX = "oauth2:user:checkSocial:%s:%s";

  String CACHE_PASSWORD_ERROR_NUM_PREFIX = "gm:aas:user:passwordErrorCount:%s";
  String CACHE_PASSWORD_ERROR_LOCKED_PREFIX = "gm:aas:user:passwordErrorLocked:%s";

  /**
   * For system token
   */
  String SYS_TOKEN_CLIENT_ID_FMT = XCAN_TENANT_PLATFORM_CODE + "_t%s_s%s";
  String SYS_TOKEN_CLIENT_DESC_FMT = "Tenant [%s] system token `%s` oauth2 client";

  /**
   * For private application open access
   *
   * @see `FeignOpen2pAuthInterceptor`
   */
  String SIGN2P_CLIENT_ID_FMT = XCAN_2P_PLATFORM_CODE + "_t%s_b%s_r%s";
  String SIGN2P_CLIENT_NAME_FMT = "Tenant[%s]-[%s][%s]";

  /**
   * 验证码模板参数名称配置
   */
  String VERIFICATION_CODE_NAME = "verificationCode";
  String VERIFICATION_CODE_VALID_MINUTE = "expiryMinutes";

  // 邮件模版编码常量配置
  String TEMPLATE_CODE_USER_INVITATION = "UserInvitation";  // 消息通知邮件模版编码
  String TEMPLATE_CODE_LOGIN_VERIFICATION = "LoginVerification"; // 登录验证码邮件模版编码
  String REGISTER_VERIFICATION_TEMPLATE_CODE = "RegisterVerification";  // 注解验证码邮件模版编码
  String TEMPLATE_CODE_RETRIEVE_PASSWORD_VERIFICATION = "RetrievePassword";  // 找回密码验证码邮件模版编码
  String TEMPLATE_CODE_SYSTEM_NOTIFICATION = "SystemNotification";  // 系统通知邮件模版编码

  // 短信验证码
  String DEFAULT_SMS_LANGUAGE = "zh-CN";
  String TEMPLATE_CODE_VERIFICATION_CODE = "VerificationCode";  // 短信验证码模版编码
  String TEMPLATE_CODE_CHANNEL_TEST = "ChannelTest"; // 短信通道测试模版编码
  String TEMPLATE_CODE_EVENT_NOTIFICATION = "EventNotification"; // 短信事件通知模版编码

  /**
   * 邮件验证码常量配置
   */
  String EMAIL_VERIFICATION_CODE_CACHE_PREFIX = "gm:email:verificationCode";
  String EMAIL_CACHE_REPEAT_CHECK_PREFIX = "gm:email:verificationCode:repeat:check";

  /**
   * 邮件模版编码常量
   */
  String DEFAULT_EMAIL_LANGUAGE = "en-US";
  String USER_EMAIL_INVITATION_TEMPLATE_CODE = "UserInvitation";  // 用户邮件邀请注册邮件模版编码

  /**
   * 短信验证码常量配置
   */
  String SMS_VC_CACHE_PREFIX = "gm:sms:verificationCode";
  String SMS_VC_CACHE_REPEAT_CHECK_PREFIX = "gm:sms:verificationCode:repeat:check";

}
