package cloud.xcan.angus.core.repo.infra.persistence.postgres.format;

import cloud.xcan.angus.core.repo.domain.format.entity.RpmPackageEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("rpmPackageEntityRepo")
public interface RpmPackageEntityRepoPostgres extends RpmPackageEntityRepo {
}
