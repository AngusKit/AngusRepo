package cloud.xcan.angus.core.gm.domain.ldap.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * LDAP类型枚举
 */
public enum LdapType implements Value<String> {
  /**
   * Active Directory
   */
  ACTIVE_DIRECTORY,
  /**
   * OpenLDAP
   */
  OPENLDAP,
  /**
   * Azure AD
   */
  AZURE_AD,
  /**
   * 通用类型
   */
  GENERIC;

  @Override
  public String getValue() {
    return this.name();
  }
}
