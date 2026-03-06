package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MavenMetadataEntityRepo extends BaseRepository<MavenMetadataEntity, Long> {

  List<MavenMetadataEntity> findByRepositoryId(Long repositoryId);

  Optional<MavenMetadataEntity> findByRepositoryIdAndGroupIdAndArtifactIdAndVersion(
      Long repositoryId, String groupId, String artifactId, String version);

  List<MavenMetadataEntity> findByRepositoryIdAndGroupIdAndArtifactId(
      Long repositoryId, String groupId, String artifactId);

  boolean existsByRepositoryIdAndGroupIdAndArtifactIdAndVersion(
      Long repositoryId, String groupId, String artifactId, String version);

  long countByRepositoryId(Long repositoryId);
}
