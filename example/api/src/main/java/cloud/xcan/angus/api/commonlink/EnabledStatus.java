package cloud.xcan.angus.api.commonlink;

import cloud.xcan.angus.spec.experimental.Value;

public enum EnabledStatus implements Value<String> {
  /**
   * 已启用
   */
  ENABLED,

  /**
   * 已禁用
   */
  DISABLED;

  @Override
  public String getValue() {
    return this.name();
  }

  public boolean isEnabled() {
    return this.equals(ENABLED);
  }
}
