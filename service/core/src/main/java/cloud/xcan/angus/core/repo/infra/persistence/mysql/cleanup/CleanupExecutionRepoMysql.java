package cloud.xcan.angus.core.repo.infra.persistence.mysql.cleanup;

import cloud.xcan.angus.core.repo.domain.cleanup.CleanupExecutionRepo;
import org.springframework.stereotype.Repository;

@Repository("cleanupExecutionRepo")
public interface CleanupExecutionRepoMysql extends CleanupExecutionRepo {

}
