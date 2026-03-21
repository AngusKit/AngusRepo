package cloud.xcan.angus.core.gm.domain.log.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * 日志文件状态枚举
 */
public enum LogStatus implements Value<String> {
  /**
   * 活跃（当前正在写入）
   */
  ACTIVE,

  /**
   * 已完成（不再写入）
   */
  COMPLETED,

  /**
   * 已归档
   */
  ARCHIVED;

  @Override
  public String getValue() {
    return name();
  }
}
