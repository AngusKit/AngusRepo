package cloud.xcan.angus.core.repo.infra.persistence.postgres.format;

import cloud.xcan.angus.core.repo.domain.format.entity.DockerBlobEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("dockerBlobEntityRepo")
public interface DockerBlobEntityRepoPostgres extends DockerBlobEntityRepo {


}
