package cloud.xcan.angus.core.repo.infra.persistence.postgres.format;

import cloud.xcan.angus.core.repo.domain.format.entity.YumRepodataEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("yumRepodataEntityRepo")
public interface YumRepodataEntityRepoPostgres extends YumRepodataEntityRepo {
}
