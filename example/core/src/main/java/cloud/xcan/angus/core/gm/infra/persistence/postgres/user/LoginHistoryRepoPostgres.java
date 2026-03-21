package cloud.xcan.angus.core.gm.infra.persistence.postgres.user;

import cloud.xcan.angus.core.gm.domain.user.LoginHistoryRepo;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginHistoryRepoPostgres extends LoginHistoryRepo {

}
