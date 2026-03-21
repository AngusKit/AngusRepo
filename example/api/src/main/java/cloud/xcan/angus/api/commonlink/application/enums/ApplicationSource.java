package cloud.xcan.angus.api.commonlink.application.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum ApplicationSource implements Value<String> {
  /**
   * 安装或开通应用
   */
  INSTALLED,
  /**
   * 用户自定义应用
   */
  CUSTOM;

  @Override
  public String getValue() {
    return this.name();
  }
}
