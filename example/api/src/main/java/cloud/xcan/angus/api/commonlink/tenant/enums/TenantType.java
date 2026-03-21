package cloud.xcan.angus.api.commonlink.tenant.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum TenantType implements Value<String> {
  /**
   * 个人
   */
  PERSONAL,

  /**
   * 企业
   */
  ENTERPRISE;

  @Override
  public String getValue() {
    return this.name();
  }

}
