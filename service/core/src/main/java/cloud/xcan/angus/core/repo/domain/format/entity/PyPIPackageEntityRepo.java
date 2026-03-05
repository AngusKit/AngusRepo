package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PyPIPackageEntityRepo extends BaseRepository<PyPIPackageEntity, Long> {

  List<PyPIPackageEntity> findByRepositoryId(Long repositoryId);

  Optional<PyPIPackageEntity> findByRepositoryIdAndNormalizedNameAndVersion(Long repositoryId, String normalizedName, String version);

  List<PyPIPackageEntity> findByRepositoryIdAndNormalizedName(Long repositoryId, String normalizedName);

  long countByRepositoryId(Long repositoryId);
}
