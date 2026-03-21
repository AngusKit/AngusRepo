package cloud.xcan.angus.core.gm.domain.ldap.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * LDAP连接状态枚举
 */
public enum LdapStatus implements Value<String> {
  /**
   * 已连接
   */
  CONNECTED,
  /**
   * 已断开
   */
  DISCONNECTED,
  /**
   * 认证中
   */
  AUTHENTICATING,
  /**
   * 错误
   */
  ERROR,
  /**
   * 已禁用
   */
  DISABLED;

  @Override
  public String getValue() {
    return this.name();
  }
}
