package cloud.xcan.angus.core.repo.infra.persistence.postgres.format;

import cloud.xcan.angus.core.repo.domain.format.entity.NpmPackageEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("npmPackageEntityRepo")
public interface NpmPackageEntityRepoPostgres extends NpmPackageEntityRepo {
}
