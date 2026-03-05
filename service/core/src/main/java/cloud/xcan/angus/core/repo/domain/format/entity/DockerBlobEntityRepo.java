package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface DockerBlobEntityRepo extends BaseRepository<DockerBlobEntity, Long> {

  Optional<DockerBlobEntity> findByDigest(String digest);

  boolean existsByDigest(String digest);
}
