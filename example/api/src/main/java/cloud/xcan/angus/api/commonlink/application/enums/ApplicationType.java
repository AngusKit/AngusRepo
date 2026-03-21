package cloud.xcan.angus.api.commonlink.application.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum ApplicationType implements Value<String> {
  /**
   * 基础应用
   */
  BASE,

  /**
   * 业务应用
   */
  BUSINESS;

  @Override
  public String getValue() {
    return this.name();
  }
}
