package cloud.xcan.angus.core.gm.domain.email.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum EmailType implements Value<String> {
  VERIFICATION,
  NOTIFICATION,
  MARKETING,
  SYSTEM,
  ALERT;

  @Override
  public String getValue() {
    return this.name();
  }
}
