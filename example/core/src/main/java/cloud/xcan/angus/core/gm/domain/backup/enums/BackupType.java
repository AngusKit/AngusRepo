package cloud.xcan.angus.core.gm.domain.backup.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum BackupType implements Value<String> {
  FULL,
  INCREMENTAL;

  @Override
  public String getValue() {
    return this.name();
  }
}
