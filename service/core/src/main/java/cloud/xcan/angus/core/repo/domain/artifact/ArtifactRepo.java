package cloud.xcan.angus.core.repo.domain.artifact;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface ArtifactRepo extends BaseRepository<Artifact, Long> {

  List<Artifact> findByRepositoryId(Long repositoryId);

  Optional<Artifact> findByRepositoryIdAndName(Long repositoryId, String name);

  boolean existsByRepositoryIdAndNameAndVersion(Long repositoryId, String name, String version);

  long countByRepositoryId(Long repositoryId);

  @Modifying
  @Query("UPDATE Artifact a SET a.downloads = a.downloads + :increment WHERE a.id = :id")
  void updateDownloadCount(@Param("id") Long id, @Param("increment") int increment);

  @Modifying
  @Query("UPDATE Artifact a SET a.stars = a.stars + :delta WHERE a.id = :id")
  void updateStarCount(@Param("id") Long id, @Param("delta") int delta);

  @Modifying
  @Query("UPDATE Artifact a SET a.isLatest = :isLatest WHERE a.id = :id")
  void updateIsLatest(@Param("id") Long id, @Param("isLatest") Boolean isLatest);
}
