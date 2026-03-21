package cloud.xcan.angus.core.gm.application.cmd.ldap;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.domain.ldap.LdapSyncHistory;

/**
 * LDAP命令服务接口
 */
public interface LdapCmd {

  /**
   * 创建LDAP配置
   */
  Ldap create(Ldap ldap);

  /**
   * 删除LDAP配置
   */
  void delete(Long id);

  /**
   * 更新LDAP配置
   */
  Ldap updateConfig(Long id, Ldap ldap);

  /**
   * 更新LDAP配置启用状态（允许同时启用多个LDAP）
   */
  Ldap updateStatus(Long id, EnabledStatus status);

  /**
   * 同步LDAP用户
   *
   * @param config   LDAP配置
   * @param history  同步历史记录（如果为null则创建新记录）
   * @param testMode 是否为测试模式（true：不保存数据到数据库，false：正常同步）
   * @return 更新后的同步历史记录
   */
  LdapSyncHistory syncUsers(Ldap config, LdapSyncHistory history, boolean testMode);

  /**
   * 同步所有已启用的LDAP配置的用户。若无已启用配置则静默返回。
   */
  void syncAllEnabledUsers();
}
