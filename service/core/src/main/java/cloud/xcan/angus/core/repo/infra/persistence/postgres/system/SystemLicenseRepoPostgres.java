package cloud.xcan.angus.core.repo.infra.persistence.postgres.system;

import cloud.xcan.angus.core.repo.domain.system.SystemLicenseRepo;
import org.springframework.stereotype.Repository;

@Repository("systemLicenseRepo")
public interface SystemLicenseRepoPostgres extends SystemLicenseRepo {
}
