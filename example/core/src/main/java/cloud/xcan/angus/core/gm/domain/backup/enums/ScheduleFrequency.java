package cloud.xcan.angus.core.gm.domain.backup.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum ScheduleFrequency implements Value<String> {

  DAILY,

  WEEKLY,

  MONTHLY;

  @Override
  public String getValue() {
    return this.name();
  }
}
