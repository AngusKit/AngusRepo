package cloud.xcan.angus.core.repo.infra.persistence.mysql.cleanup;

import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicyRepo;
import org.springframework.stereotype.Repository;

@Repository("cleanupPolicyRepo")
public interface CleanupPolicyRepoMysql extends CleanupPolicyRepo {

}
