package cloud.xcan.angus.core.repo.infra.persistence.postgres.security;

import cloud.xcan.angus.core.repo.domain.security.ScanPolicyRepo;
import org.springframework.stereotype.Repository;

@Repository("scanPolicyRepo")
public interface ScanPolicyRepoPostgres extends ScanPolicyRepo {
}
