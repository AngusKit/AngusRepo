package cloud.xcan.angus.api.commonlink.oauthuser.enums;

import cloud.xcan.angus.spec.experimental.Value;
import lombok.Getter;

@Getter
public enum SignupType implements Value<String> {

  MOBILE,
  EMAIL,
  NOOP;

  public boolean isMobile() {
    return MOBILE.equals(this);
  }

  public boolean isEmail() {
    return EMAIL.equals(this);
  }

  @Override
  public String getValue() {
    return this.name();
  }
}
