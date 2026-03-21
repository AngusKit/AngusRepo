package cloud.xcan.angus.api.commonlink.group.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum GroupType implements Value<String> {
  /**
   * 项目组
   */
  PROJECT,

  /**
   * 职能组
   */
  FUNCTION,

  /**
   * 临时组
   */
  TEMP;

  @Override
  public String getValue() {
    return this.name();
  }
}
