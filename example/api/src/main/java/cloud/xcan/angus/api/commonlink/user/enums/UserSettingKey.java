package cloud.xcan.angus.api.commonlink.user.enums;

import cloud.xcan.angus.spec.experimental.Value;
import lombok.Getter;

@Getter
public enum UserSettingKey implements Value<String> {
  APPEARANCE,
  SECURITY,
  NOTIFICATION;

  @Override
  public String getValue() {
    return this.name();
  }
}
