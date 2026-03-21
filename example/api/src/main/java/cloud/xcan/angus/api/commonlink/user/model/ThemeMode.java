package cloud.xcan.angus.api.commonlink.user.model;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * 主题模式枚举
 */
public enum ThemeMode implements Value<String> {
  LIGHT,      // 浅色模式
  DARK,       // 深色模式
  AUTO;        // 自动（跟随系统）

  @Override
  public String getValue() {
    return this.name();
  }
}
