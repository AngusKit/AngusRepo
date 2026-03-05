package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface DockerImageEntityRepo extends BaseRepository<DockerImageEntity, Long> {

  List<DockerImageEntity> findByRepositoryId(Long repositoryId);

  Optional<DockerImageEntity> findByRepositoryIdAndDigest(Long repositoryId, String digest);

  Optional<DockerImageEntity> findByRepositoryIdAndImageNameAndTag(
      Long repositoryId, String imageName, String tag);

  long countByRepositoryId(Long repositoryId);
}
