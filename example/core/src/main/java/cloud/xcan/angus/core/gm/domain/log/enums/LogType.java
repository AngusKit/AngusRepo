package cloud.xcan.angus.core.gm.domain.log.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * 系统日志类型枚举
 */
public enum LogType implements Value<String> {
  /**
   * 应用日志
   */
  APPLICATION,

  /**
   * 错误日志
   */
  ERROR,

  /**
   * 控制台日志
   */
  CONSOLE,

  /**
   * 其他日志
   */
  OTHER;

  @Override
  public String getValue() {
    return name();
  }
}
