package cloud.xcan.angus.core.repo.infra.persistence.postgres.artifact;

import cloud.xcan.angus.core.repo.domain.artifact.ArtifactRepo;
import org.springframework.stereotype.Repository;

@Repository("artifactRepo")
public interface ArtifactRepoPostgres extends ArtifactRepo {


}
