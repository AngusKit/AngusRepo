package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RpmPackageEntityRepo extends BaseRepository<RpmPackageEntity, Long> {

  List<RpmPackageEntity> findByRepositoryId(Long repositoryId);

  Optional<RpmPackageEntity> findByRepositoryIdAndNameAndEpochAndVersionAndReleaseVerAndArch(
      Long repositoryId, String name, Integer epoch, String version, String releaseVer, String arch);

  long countByRepositoryId(Long repositoryId);
}
