package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NpmVersionEntityRepo extends BaseRepository<NpmVersionEntity, Long> {

  List<NpmVersionEntity> findByPackageId(Long packageId);

  Optional<NpmVersionEntity> findByPackageIdAndVersion(Long packageId, String version);

  boolean existsByPackageIdAndVersion(Long packageId, String version);
}
