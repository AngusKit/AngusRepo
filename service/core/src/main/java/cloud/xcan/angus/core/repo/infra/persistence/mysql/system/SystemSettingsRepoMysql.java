package cloud.xcan.angus.core.repo.infra.persistence.mysql.system;

import cloud.xcan.angus.core.repo.domain.system.SystemSettingsRepo;
import org.springframework.stereotype.Repository;

@Repository("systemSettingsRepo")
public interface SystemSettingsRepoMysql extends SystemSettingsRepo {
}
