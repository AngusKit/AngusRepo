package cloud.xcan.angus.core.gm.domain.ldap.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * LDAP同步状态枚举
 */
public enum LdapSyncStatus implements Value<String> {
  /**
   * 运行中
   */
  RUNNING,
  /**
   * 成功
   */
  SUCCESS,
  /**
   * 失败
   */
  FAILED,
  /**
   * 已取消
   */
  CANCELLED;

  @Override
  public String getValue() {
    return this.name();
  }
}


