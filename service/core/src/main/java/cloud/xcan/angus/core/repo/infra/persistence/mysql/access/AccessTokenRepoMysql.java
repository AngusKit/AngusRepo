package cloud.xcan.angus.core.repo.infra.persistence.mysql.access;

import cloud.xcan.angus.core.repo.domain.access.AccessTokenRepo;
import org.springframework.stereotype.Repository;

@Repository("accessTokenRepo")
public interface AccessTokenRepoMysql extends AccessTokenRepo {


}
