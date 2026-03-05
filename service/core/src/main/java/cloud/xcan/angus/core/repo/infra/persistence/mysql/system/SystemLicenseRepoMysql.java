package cloud.xcan.angus.core.repo.infra.persistence.mysql.system;

import cloud.xcan.angus.core.repo.domain.system.SystemLicenseRepo;
import org.springframework.stereotype.Repository;

@Repository("systemLicenseRepo")
public interface SystemLicenseRepoMysql extends SystemLicenseRepo {
}
