package cloud.xcan.angus.core.gm.domain.backup.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum RestoreStatus implements Value<String> {
  IN_PROGRESS,   // 进行中
  SUCCESS,       // 成功
  FAILED;        // 失败

  @Override
  public String getValue() {
    return this.name();
  }
}
