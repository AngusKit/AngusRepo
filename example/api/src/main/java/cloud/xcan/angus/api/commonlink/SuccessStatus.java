package cloud.xcan.angus.api.commonlink;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * 登录状态枚举
 */
public enum SuccessStatus implements Value<String> {
  /**
   * 成功
   */
  SUCCESS,

  /**
   * 失败
   */
  FAILED;

  @Override
  public String getValue() {
    return this.name();
  }
}
