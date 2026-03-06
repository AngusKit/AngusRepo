package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NuGetPackageEntityRepo extends BaseRepository<NuGetPackageEntity, Long> {

  List<NuGetPackageEntity> findByRepositoryId(Long repositoryId);

  Optional<NuGetPackageEntity> findByRepositoryIdAndPackageIdAndVersion(Long repositoryId, String packageId, String version);

  List<NuGetPackageEntity> findByRepositoryIdAndPackageId(Long repositoryId, String packageId);

  long countByRepositoryId(Long repositoryId);

  List<NuGetPackageEntity> findByRepositoryIdAndIsListedTrue(Long repositoryId);
}
