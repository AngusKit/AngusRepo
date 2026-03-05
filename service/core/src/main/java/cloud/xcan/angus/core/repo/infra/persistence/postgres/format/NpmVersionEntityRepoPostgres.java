package cloud.xcan.angus.core.repo.infra.persistence.postgres.format;

import cloud.xcan.angus.core.repo.domain.format.entity.NpmVersionEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("npmVersionEntityRepo")
public interface NpmVersionEntityRepoPostgres extends NpmVersionEntityRepo {
}
