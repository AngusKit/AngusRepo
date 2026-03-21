package cloud.xcan.angus.core.gm.domain.interfaces.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum InterfaceMonitoringType implements Value<String> {
  /**
   * 请求监控
   */
  REQUEST,

  /**
   * 响应监控
   */
  RESPONSE,

  /**
   * 性能监控
   */
  PERFORMANCE,

  /**
   * 错误监控
   */
  ERROR,

  /**
   * 安全监控
   */
  SECURITY,

  /**
   * 限流监控
   */
  RATE_LIMIT;

  @Override
  public String getValue() {
    return this.name();
  }
}
