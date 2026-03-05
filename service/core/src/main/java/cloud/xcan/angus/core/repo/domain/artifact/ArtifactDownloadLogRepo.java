package cloud.xcan.angus.core.repo.domain.artifact;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ArtifactDownloadLogRepo extends BaseRepository<ArtifactDownloadLog, Long> {

  List<ArtifactDownloadLog> findByArtifactId(Long artifactId);

  long countByArtifactId(Long artifactId);
}
