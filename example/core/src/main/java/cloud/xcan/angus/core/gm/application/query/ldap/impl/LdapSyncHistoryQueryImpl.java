package cloud.xcan.angus.core.gm.application.query.ldap.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.ldap.LdapSyncHistoryQuery;
import cloud.xcan.angus.core.gm.domain.ldap.LdapSyncHistory;
import cloud.xcan.angus.core.gm.domain.ldap.LdapSyncHistoryRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * LDAP同步历史查询服务实现
 */
@Service
@Transactional(readOnly = true)
public class LdapSyncHistoryQueryImpl implements LdapSyncHistoryQuery {

  @Resource
  private LdapSyncHistoryRepo ldapSyncHistoryRepo;

  @Override
  public LdapSyncHistory findAndCheck(Long id) {
    return new BizTemplate<LdapSyncHistory>() {
      @Override
      protected LdapSyncHistory process() {
        return ldapSyncHistoryRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("LDAP同步历史「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Page<LdapSyncHistory> find(GenericSpecification<LdapSyncHistory> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<LdapSyncHistory>>() {
      @Override
      protected Page<LdapSyncHistory> process() {
        return ldapSyncHistoryRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  /**
   * 根据LDAP配置ID查找同步历史
   */
  public List<LdapSyncHistory> findByLdapId(Long ldapId) {
    return new BizTemplate<List<LdapSyncHistory>>() {
      @Override
      protected List<LdapSyncHistory> process() {
        return ldapSyncHistoryRepo.findByLdapId(ldapId);
      }
    }.execute();
  }

  /**
   * 查找最新的同步历史
   */
  public LdapSyncHistory findLatestByLdapId(Long ldapId) {
    return new BizTemplate<LdapSyncHistory>() {
      @Override
      protected LdapSyncHistory process() {
        return ldapSyncHistoryRepo.findFirstByLdapIdOrderByStartTimeDesc(ldapId);
      }
    }.execute();
  }
}
