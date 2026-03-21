package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.gm.domain.user.UserSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserSearchRepoMysql extends SimpleSearchRepository<User>
    implements UserSearchRepo {

}
