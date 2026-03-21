package cloud.xcan.angus.api.commonlink.sms;

import cloud.xcan.angus.spec.experimental.Value;

public enum SmsStatus implements Value<String> {
  /**
   * 待发送
   */
  PENDING,

  /**
   * 发送中
   */
  SENDING,

  /**
   * 已发送
   */
  SENT,

  /**
   * 已送达
   */
  DELIVERED,

  /**
   * 发送失败
   */
  FAILED,

  /**
   * 已取消
   */
  CANCELLED;

  @Override
  public String getValue() {
    return this.name();
  }

  public boolean isFailed() {
    return this.equals(FAILED);
  }
}
