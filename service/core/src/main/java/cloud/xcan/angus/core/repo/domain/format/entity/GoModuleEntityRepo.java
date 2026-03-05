package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GoModuleEntityRepo extends BaseRepository<GoModuleEntity, Long> {

  List<GoModuleEntity> findByRepositoryId(Long repositoryId);

  Optional<GoModuleEntity> findByRepositoryIdAndModulePathAndVersion(Long repositoryId, String modulePath, String version);

  List<GoModuleEntity> findByRepositoryIdAndModulePath(Long repositoryId, String modulePath);

  long countByRepositoryId(Long repositoryId);
}
