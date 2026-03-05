package cloud.xcan.angus.core.repo.infra.persistence.postgres.format;

import cloud.xcan.angus.core.repo.domain.format.entity.PyPIFileEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("pyPIFileEntityRepo")
public interface PyPIFileEntityRepoPostgres extends PyPIFileEntityRepo {
}
