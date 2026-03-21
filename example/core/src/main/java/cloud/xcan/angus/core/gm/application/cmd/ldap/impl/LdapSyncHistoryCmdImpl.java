package cloud.xcan.angus.core.gm.application.cmd.ldap.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.ldap.LdapSyncHistoryCmd;
import cloud.xcan.angus.core.gm.domain.ldap.LdapSyncHistory;
import cloud.xcan.angus.core.gm.domain.ldap.LdapSyncHistoryRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * LDAP同步历史命令服务实现
 */
@Service
public class LdapSyncHistoryCmdImpl extends CommCmd<LdapSyncHistory, Long> implements
    LdapSyncHistoryCmd {

  @Resource
  private LdapSyncHistoryRepo ldapSyncHistoryRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public LdapSyncHistory create(LdapSyncHistory history) {
    return new BizTemplate<LdapSyncHistory>() {
      @Override
      protected LdapSyncHistory process() {
        return insert(history);
      }
    }.execute();
  }

  @Override
  protected BaseRepository<LdapSyncHistory, Long> getRepository() {
    return ldapSyncHistoryRepo;
  }
}
