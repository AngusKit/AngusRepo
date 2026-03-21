package cloud.xcan.angus.core.gm.domain.system.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * 告警记录状态枚举
 */
public enum AlertRecordStatus implements Value<String> {
  ACTIVE,    // 活跃中
  RESOLVED;  // 已解决

  @Override
  public String getValue() {
    return this.name();
  }
}
