package cloud.xcan.angus.core.repo.infra.persistence.postgres.format;

import cloud.xcan.angus.core.repo.domain.format.entity.AptPackageEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("aptPackageEntityRepo")
public interface AptPackageEntityRepoPostgres extends AptPackageEntityRepo {
}
