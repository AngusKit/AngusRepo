package cloud.xcan.angus.core.repo.infra.persistence.postgres.format;

import cloud.xcan.angus.core.repo.domain.format.entity.DockerImageEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("dockerImageEntityRepo")
public interface DockerImageEntityRepoPostgres extends DockerImageEntityRepo {


}
