package cloud.xcan.angus.core.gm.application.query.ldap;

import cloud.xcan.angus.core.gm.domain.ldap.LdapSyncHistory;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * LDAP同步历史查询服务接口
 */
public interface LdapSyncHistoryQuery {

  /**
   * 根据ID查找同步历史并检查存在性
   */
  LdapSyncHistory findAndCheck(Long id);

  /**
   * 分页查询同步历史
   */
  Page<LdapSyncHistory> find(GenericSpecification<LdapSyncHistory> spec, PageRequest pageable);

}
