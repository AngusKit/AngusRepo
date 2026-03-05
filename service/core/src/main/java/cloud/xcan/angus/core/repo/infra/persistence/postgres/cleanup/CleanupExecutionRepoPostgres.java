package cloud.xcan.angus.core.repo.infra.persistence.postgres.cleanup;

import cloud.xcan.angus.core.repo.domain.cleanup.CleanupExecutionRepo;
import org.springframework.stereotype.Repository;

@Repository("cleanupExecutionRepo")
public interface CleanupExecutionRepoPostgres extends CleanupExecutionRepo {

}
