package cloud.xcan.angus.core.repo.infra.persistence.postgres.cleanup;

import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicyRepo;
import org.springframework.stereotype.Repository;

@Repository("cleanupPolicyRepo")
public interface CleanupPolicyRepoPostgres extends CleanupPolicyRepo {

}
