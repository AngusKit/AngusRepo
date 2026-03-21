package cloud.xcan.angus.api.commonlink.user.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * 用户来源枚举
 */
public enum UserSource implements Value<String> {
  /**
   * 平台注册
   */
  PLATFORM_REGISTER,
  /**
   * 管理员添加
   */
  ADMIN_ADDED,
  /**
   * LDAP同步
   */
  LDAP_SYNC;

  @Override
  public String getValue() {
    return this.name();
  }
}
