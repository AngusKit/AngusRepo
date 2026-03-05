package cloud.xcan.angus.core.repo.domain.artifact;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ArtifactStarRepo extends BaseRepository<ArtifactStar, Long> {

  Optional<ArtifactStar> findByArtifactIdAndUserId(Long artifactId, Long userId);

  boolean existsByArtifactIdAndUserId(Long artifactId, Long userId);

  long countByArtifactId(Long artifactId);

  void deleteByArtifactIdAndUserId(Long artifactId, Long userId);
}
