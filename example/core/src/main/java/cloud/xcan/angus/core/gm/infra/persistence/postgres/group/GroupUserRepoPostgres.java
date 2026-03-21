package cloud.xcan.angus.core.gm.infra.persistence.postgres.group;

import cloud.xcan.angus.api.commonlink.group.GroupUserRepo;
import org.springframework.stereotype.Repository;

@Repository("groupUserRepo")
public interface GroupUserRepoPostgres extends GroupUserRepo {

}
