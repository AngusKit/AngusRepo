package cloud.xcan.angus.core.repo.infra.persistence.mysql.artifact;

import cloud.xcan.angus.core.repo.domain.artifact.ArtifactRepo;
import org.springframework.stereotype.Repository;

@Repository("artifactRepo")
public interface ArtifactRepoMysql extends ArtifactRepo {


}
