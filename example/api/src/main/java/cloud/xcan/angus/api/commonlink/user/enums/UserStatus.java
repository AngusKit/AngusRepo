package cloud.xcan.angus.api.commonlink.user.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum UserStatus implements Value<String> {
  /**
   * 已激活
   */
  ACTIVE,

  /**
   * 已禁用
   */
  DISABLED,

  /**
   * 待接收
   */
  PENDING;

  public boolean isValid() {
    return this.equals(ACTIVE);
  }

  public boolean isPending() {
    return this.equals(PENDING);
  }

  @Override
  public String getValue() {
    return this.name();
  }
}
