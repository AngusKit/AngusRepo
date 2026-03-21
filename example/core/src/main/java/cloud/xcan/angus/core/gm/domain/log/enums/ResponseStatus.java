package cloud.xcan.angus.core.gm.domain.log.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * 操作响应状态枚举
 */
public enum ResponseStatus implements Value<String> {
  /**
   * 成功
   */
  SUCCESS,

  /**
   * 失败
   */
  FAILURE;

  @Override
  public String getValue() {
    return name();
  }
}
