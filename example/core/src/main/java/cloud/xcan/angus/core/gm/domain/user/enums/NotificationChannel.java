package cloud.xcan.angus.core.gm.domain.user.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * 通知渠道枚举
 */
public enum NotificationChannel implements Value<String> {
  EMAIL,      // 邮件
  PUSH,       // 推送
  DESKTOP,    // 桌面
  SMS;         // 短信

  @Override
  public String getValue() {
    return this.name();
  }
}
