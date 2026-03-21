package cloud.xcan.angus.core.gm.domain;


public interface TipMessage {

  // 认证相关消息Keys
  String CLIENT_IS_DISABLED_T = "xcm.gm.client.is.disabled.t";
  String PASSWORD_IS_TOO_SHORT_T = "xcm.gm.password.is.too.short.t";
  String LOGIN_ACCOUNT_EMPTY = "xcm.gm.login.account.empty";
  String LOGIN_DEVICE_ID_EMPTY = "xcm.gm.login.deviceId.empty";
  String LOGIN_PASSWORD_ERROR = "xcm.gm.login.password.error";
  String LOGIN_PASSWORD_ERROR_OVER_LIMIT_CODE = "BAA405";
  String LOGIN_PASSWORD_ERROR_OVER_LIMIT_T = "xcm.gm.login.password.error.over.limit.t";
  String LOGIN_PASSWORD_ERROR_LOCKED_RETRY_CODE = "BAA406";
  String LOGIN_PASSWORD_ERROR_LOCKED_RETRY_T = "xcm.gm.login.password.error.locked.retry.t";
  String LINK_SECRET_TIMEOUT = "xcm.gm.linkSecret.timeout";
  String LINK_SECRET_ILLEGAL = "xcm.gm.linkSecret.illegal";

  // 邮件相关消息Keys
  String EMAIL_VERIFY_CODE_EMPTY = "xcm.gm.email.verifyCode.empty";
  String EMAIL_VERIFY_CODE_EXPIRED = "xcm.gm.email.verifyCode.expired";
  String EMAIL_VERIFY_CODE_ERROR = "xcm.gm.email.verifyCode.error";

  // 短信相关消息Keys
  String SMS_VERIFY_CODE_EMPTY = "xcm.gm.sms.verifyCode.empty";
  String SMS_VERIFY_CODE_EXPIRED = "xcm.gm.sms.verifyCode.expired";
  String SMS_VERIFY_CODE_ERROR = "xcm.gm.sms.verifyCode.error";
}
