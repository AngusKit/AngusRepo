package cloud.xcan.angus.core.repo.domain.repository;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RepoEntityRepo extends BaseRepository<RepoEntity, Long> {

  Optional<RepoEntity> findByName(String name);

  boolean existsByName(String name);

  long countByFormat(RepositoryFormat format);

  long countByStatus(RepositoryStatus status);
}
