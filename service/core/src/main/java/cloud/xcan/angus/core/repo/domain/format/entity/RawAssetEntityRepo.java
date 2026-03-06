package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RawAssetEntityRepo extends BaseRepository<RawAssetEntity, Long> {

  List<RawAssetEntity> findByRepositoryId(Long repositoryId);

  Optional<RawAssetEntity> findByRepositoryIdAndPath(Long repositoryId, String path);

  boolean existsByRepositoryIdAndPath(Long repositoryId, String path);

  long countByRepositoryId(Long repositoryId);
}
