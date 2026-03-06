package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AptReleaseEntityRepo extends BaseRepository<AptReleaseEntity, Long> {

  Optional<AptReleaseEntity> findByRepositoryIdAndDistribution(Long repositoryId, String distribution);
}
