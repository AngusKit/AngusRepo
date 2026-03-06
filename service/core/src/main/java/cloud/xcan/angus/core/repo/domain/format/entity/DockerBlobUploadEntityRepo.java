package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface DockerBlobUploadEntityRepo extends BaseRepository<DockerBlobUploadEntity, Long> {

  Optional<DockerBlobUploadEntity> findByUuid(String uuid);

  void deleteByUuid(String uuid);
}
