package cloud.xcan.angus.core.gm.domain.user.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * 通知类型枚举
 */
public enum NotificationType implements Value<String> {
  COMMENTS,       // 评论
  MENTIONS,       // @提及
  UPDATES,        // 更新
  PRODUCT_NEWS,   // 产品新闻
  SYSTEM_ALERT;    // 系统告警

  @Override
  public String getValue() {
    return this.name();
  }
}
