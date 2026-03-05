package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PyPIFileEntityRepo extends BaseRepository<PyPIFileEntity, Long> {

  List<PyPIFileEntity> findByPackageId(Long packageId);

  Optional<PyPIFileEntity> findByPackageIdAndFilename(Long packageId, String filename);

  boolean existsByPackageIdAndFilename(Long packageId, String filename);
}
