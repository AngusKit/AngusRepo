package cloud.xcan.angus.core.gm.domain.log.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * HTTP状态码范围枚举
 */
public enum StatusRange implements Value<String> {
  /**
   * 2xx 成功
   */
  SUCCESS_2XX,

  /**
   * 4xx 客户端错误
   */
  CLIENT_ERROR_4XX,

  /**
   * 5xx 服务端错误
   */
  SERVER_ERROR_5XX;

  @Override
  public String getValue() {
    return name();
  }
}
