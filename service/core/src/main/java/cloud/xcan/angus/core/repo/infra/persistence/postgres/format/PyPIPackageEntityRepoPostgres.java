package cloud.xcan.angus.core.repo.infra.persistence.postgres.format;

import cloud.xcan.angus.core.repo.domain.format.entity.PyPIPackageEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("pyPIPackageEntityRepo")
public interface PyPIPackageEntityRepoPostgres extends PyPIPackageEntityRepo {
}
