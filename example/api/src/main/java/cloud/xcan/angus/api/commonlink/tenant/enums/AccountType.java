package cloud.xcan.angus.api.commonlink.tenant.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum AccountType implements Value<String> {
  /**
   * 主账号
   */
  MAIN,

  /**
   * 子账号
   */
  SUB;

  @Override
  public String getValue() {
    return this.name();
  }

  public boolean isMainAccount() {
    return this.equals(MAIN);
  }
}
