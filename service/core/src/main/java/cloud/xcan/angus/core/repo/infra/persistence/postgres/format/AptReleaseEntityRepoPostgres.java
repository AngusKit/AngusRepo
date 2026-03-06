package cloud.xcan.angus.core.repo.infra.persistence.postgres.format;

import cloud.xcan.angus.core.repo.domain.format.entity.AptReleaseEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("aptReleaseEntityRepo")
public interface AptReleaseEntityRepoPostgres extends AptReleaseEntityRepo {
}
