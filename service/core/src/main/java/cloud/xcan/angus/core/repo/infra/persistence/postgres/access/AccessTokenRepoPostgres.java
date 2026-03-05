package cloud.xcan.angus.core.repo.infra.persistence.postgres.access;

import cloud.xcan.angus.core.repo.domain.access.AccessTokenRepo;
import org.springframework.stereotype.Repository;

@Repository("accessTokenRepo")
public interface AccessTokenRepoPostgres extends AccessTokenRepo {


}
