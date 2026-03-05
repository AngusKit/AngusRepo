package cloud.xcan.angus.core.repo.infra.persistence.mysql.user;

import cloud.xcan.angus.core.repo.domain.user.UserProfileRepo;
import org.springframework.stereotype.Repository;

@Repository("userProfileRepo")
public interface UserProfileRepoMysql extends UserProfileRepo {
}
