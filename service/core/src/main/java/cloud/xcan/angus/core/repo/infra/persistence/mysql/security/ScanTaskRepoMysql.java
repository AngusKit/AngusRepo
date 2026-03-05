package cloud.xcan.angus.core.repo.infra.persistence.mysql.security;

import cloud.xcan.angus.core.repo.domain.security.ScanTaskRepo;
import org.springframework.stereotype.Repository;

@Repository("scanTaskRepo")
public interface ScanTaskRepoMysql extends ScanTaskRepo {
}
