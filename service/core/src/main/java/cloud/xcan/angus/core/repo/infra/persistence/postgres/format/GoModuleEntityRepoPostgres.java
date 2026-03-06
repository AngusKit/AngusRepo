package cloud.xcan.angus.core.repo.infra.persistence.postgres.format;

import cloud.xcan.angus.core.repo.domain.format.entity.GoModuleEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("goModuleEntityRepo")
public interface GoModuleEntityRepoPostgres extends GoModuleEntityRepo {
}
