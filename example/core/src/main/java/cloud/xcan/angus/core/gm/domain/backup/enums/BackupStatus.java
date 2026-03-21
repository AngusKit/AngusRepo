package cloud.xcan.angus.core.gm.domain.backup.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum BackupStatus implements Value<String> {
  PENDING,
  IN_PROGRESS,
  COMPLETED,
  FAILED,
  CANCELLED,
  RESTORING;

  @Override
  public String getValue() {
    return this.name();
  }
}
