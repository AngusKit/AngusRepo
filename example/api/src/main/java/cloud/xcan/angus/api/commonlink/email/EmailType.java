package cloud.xcan.angus.api.commonlink.email;

import cloud.xcan.angus.spec.experimental.Value;


public enum EmailType implements Value<String> {
  TEMPLATE, CUSTOM;

  @Override
  public String getValue() {
    return this.name();
  }
}
