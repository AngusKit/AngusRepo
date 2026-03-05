package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NpmPackageEntityRepo extends BaseRepository<NpmPackageEntity, Long> {

  List<NpmPackageEntity> findByRepositoryId(Long repositoryId);

  Optional<NpmPackageEntity> findByRepositoryIdAndName(Long repositoryId, String name);

  long countByRepositoryId(Long repositoryId);

  List<NpmPackageEntity> findByRepositoryIdAndScope(Long repositoryId, String scope);
}
