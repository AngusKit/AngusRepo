package cloud.xcan.angus.api.commonlink.setting.alert;

import cloud.xcan.angus.spec.experimental.Value;

public enum AlertLevel implements Value<String> {
  HIGH,
  MEDIUM,
  LOW;

  @Override
  public String getValue() {
    return this.name();
  }
}
