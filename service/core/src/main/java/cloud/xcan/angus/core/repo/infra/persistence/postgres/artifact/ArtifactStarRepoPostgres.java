package cloud.xcan.angus.core.repo.infra.persistence.postgres.artifact;

import cloud.xcan.angus.core.repo.domain.artifact.ArtifactStarRepo;
import org.springframework.stereotype.Repository;

@Repository("artifactStarRepo")
public interface ArtifactStarRepoPostgres extends ArtifactStarRepo {


}
