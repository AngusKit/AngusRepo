package cloud.xcan.angus.api.commonlink.user.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * 令牌状态枚举
 */
public enum TokenStatus implements Value<String> {
  ACTIVE,     // 活跃
  EXPIRED,    // 已过期
  REVOKED;     // 已撤销

  @Override
  public String getValue() {
    return this.name();
  }
}
