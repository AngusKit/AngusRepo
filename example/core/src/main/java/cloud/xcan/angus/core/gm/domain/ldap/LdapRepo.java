package cloud.xcan.angus.core.gm.domain.ldap;

import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapStatus;
import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;

public interface LdapRepo extends BaseRepository<Ldap, Long> {

  Optional<Ldap> findByName(String name);

  List<Ldap> findByType(LdapType type);

  List<Ldap> findByStatus(LdapStatus status);

  List<Ldap> findByEnabled(Boolean enabled);

}
