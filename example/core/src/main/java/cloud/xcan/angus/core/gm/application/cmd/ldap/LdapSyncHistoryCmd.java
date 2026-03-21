package cloud.xcan.angus.core.gm.application.cmd.ldap;

import cloud.xcan.angus.core.gm.domain.ldap.LdapSyncHistory;

/**
 * LDAP同步历史命令服务接口
 */
public interface LdapSyncHistoryCmd {

  /**
   * 创建同步历史记录
   */
  LdapSyncHistory create(LdapSyncHistory history);

}
