package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.role.Role;
import cloud.xcan.angus.core.gm.domain.role.RoleSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class RoleSearchRepoMysql extends SimpleSearchRepository<Role>
    implements RoleSearchRepo {

}
