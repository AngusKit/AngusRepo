package cloud.xcan.angus.core.gm.domain.ldap;

import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapSyncStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * LDAP同步历史仓储接口
 */
@NoRepositoryBean
public interface LdapSyncHistoryRepo extends BaseRepository<LdapSyncHistory, Long> {

  /**
   * 根据LDAP配置ID查找同步历史
   */
  List<LdapSyncHistory> findByLdapId(Long ldapId);

  /**
   * 根据状态查找同步历史
   */
  List<LdapSyncHistory> findByStatus(LdapSyncStatus status);

  /**
   * 根据状态分页查找同步历史
   */
  Page<LdapSyncHistory> findByStatus(LdapSyncStatus status, Pageable pageable);

  /**
   * 查找最新的同步历史
   */
  LdapSyncHistory findFirstByLdapIdOrderByStartTimeDesc(Long ldapId);
}
