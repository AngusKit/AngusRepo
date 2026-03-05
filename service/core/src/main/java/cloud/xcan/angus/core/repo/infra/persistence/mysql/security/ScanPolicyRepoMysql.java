package cloud.xcan.angus.core.repo.infra.persistence.mysql.security;

import cloud.xcan.angus.core.repo.domain.security.ScanPolicyRepo;
import org.springframework.stereotype.Repository;

@Repository("scanPolicyRepo")
public interface ScanPolicyRepoMysql extends ScanPolicyRepo {
}
