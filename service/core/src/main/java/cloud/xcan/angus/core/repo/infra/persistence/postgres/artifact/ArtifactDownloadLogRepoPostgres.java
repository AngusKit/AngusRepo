package cloud.xcan.angus.core.repo.infra.persistence.postgres.artifact;

import cloud.xcan.angus.core.repo.domain.artifact.ArtifactDownloadLogRepo;
import org.springframework.stereotype.Repository;

@Repository("artifactDownloadLogRepo")
public interface ArtifactDownloadLogRepoPostgres extends ArtifactDownloadLogRepo {


}
