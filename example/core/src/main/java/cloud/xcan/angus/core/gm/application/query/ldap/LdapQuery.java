package cloud.xcan.angus.core.gm.application.query.ldap;

import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapStatus;
import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapType;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * LDAP查询服务接口
 */
public interface LdapQuery {

  Optional<Ldap> findById(Long id);

  Ldap findAndCheck(Long id);

  Optional<Ldap> findByName(String name);

  List<Ldap> findByType(LdapType type);

  List<Ldap> findByStatus(LdapStatus status);

  List<Ldap> findAll();

  List<Ldap> findByEnabled(Boolean enabled);

  Ldap getCurrentConfig();

  Map<String, String> getFieldMapping(Long id);
}
