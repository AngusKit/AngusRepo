package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.authorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorizationSearchRepoMysql extends SimpleSearchRepository<Authorization>
    implements AuthorizationSearchRepo {

}
