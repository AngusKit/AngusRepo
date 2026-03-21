package cloud.xcan.angus.core.gm.domain.log.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * 日志级别枚举
 */
public enum LogLevel implements Value<String> {
  /**
   * 调试
   */
  DEBUG,

  /**
   * 信息
   */
  INFO,

  /**
   * 警告
   */
  WARN,

  /**
   * 错误
   */
  ERROR,

  /**
   * 致命错误
   */
  FATAL;

  @Override
  public String getValue() {
    return name();
  }
}
