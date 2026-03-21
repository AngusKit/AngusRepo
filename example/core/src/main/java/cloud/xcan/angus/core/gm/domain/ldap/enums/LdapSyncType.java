package cloud.xcan.angus.core.gm.domain.ldap.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * LDAP同步类型枚举
 */
public enum LdapSyncType implements Value<String> {
  /**
   * 手动同步
   */
  MANUAL,
  /**
   * 自动同步
   */
  AUTO;

  @Override
  public String getValue() {
    return this.name();
  }
}
