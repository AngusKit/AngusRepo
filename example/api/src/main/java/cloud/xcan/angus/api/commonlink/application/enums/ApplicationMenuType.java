package cloud.xcan.angus.api.commonlink.application.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum ApplicationMenuType implements Value<String> {
  /**
   * 菜案
   */
  MENU,

  /**
   * 操作按钮
   */
  BUTTON,

  /**
   * 面板
   */
  PANEL;

  @Override
  public String getValue() {
    return this.name();
  }
}

