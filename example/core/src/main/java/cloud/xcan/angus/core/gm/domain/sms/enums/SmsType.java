package cloud.xcan.angus.core.gm.domain.sms.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum SmsType implements Value<String> {
  /**
   * 验证码短信
   */
  VERIFICATION,

  /**
   * 通知短信
   */
  NOTIFICATION,

  /**
   * 营销短信
   */
  MARKETING,

  /**
   * 系统短信
   */
  SYSTEM,

  /**
   * 测试短信
   */
  TEST;

  @Override
  public String getValue() {
    return this.name();
  }
}
