package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AptPackageEntityRepo extends BaseRepository<AptPackageEntity, Long> {

  List<AptPackageEntity> findByRepositoryId(Long repositoryId);

  List<AptPackageEntity> findByRepositoryIdAndDistributionAndComponentAndArchitecture(
      Long repositoryId, String distribution, String component, String architecture);

  long countByRepositoryId(Long repositoryId);
}
