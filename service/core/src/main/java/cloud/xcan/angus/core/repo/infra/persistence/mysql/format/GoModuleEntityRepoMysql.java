package cloud.xcan.angus.core.repo.infra.persistence.mysql.format;

import cloud.xcan.angus.core.repo.domain.format.entity.GoModuleEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("goModuleEntityRepo")
public interface GoModuleEntityRepoMysql extends GoModuleEntityRepo {
}
