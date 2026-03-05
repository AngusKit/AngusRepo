package cloud.xcan.angus.core.repo.infra.persistence.postgres.user;

import cloud.xcan.angus.core.repo.domain.user.UserApiTokenRepo;
import org.springframework.stereotype.Repository;

@Repository("userApiTokenRepo")
public interface UserApiTokenRepoPostgres extends UserApiTokenRepo {
}
