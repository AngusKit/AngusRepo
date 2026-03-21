package cloud.xcan.angus.api.commonlink.client.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum Client2pSignupBiz implements Value<String> {
  STORE, AGENT;

  @Override
  public String getValue() {
    return this.name();
  }
}
