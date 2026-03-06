package cloud.xcan.angus.core.repo.infra.persistence.postgres.format;

import cloud.xcan.angus.core.repo.domain.format.entity.NuGetPackageEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("nuGetPackageEntityRepo")
public interface NuGetPackageEntityRepoPostgres extends NuGetPackageEntityRepo {
}
