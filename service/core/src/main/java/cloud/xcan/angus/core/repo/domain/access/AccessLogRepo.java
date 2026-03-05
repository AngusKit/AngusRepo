package cloud.xcan.angus.core.repo.domain.access;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AccessLogRepo extends BaseRepository<AccessLog, Long> {

  List<AccessLog> findByRepositoryId(Long repositoryId);

  long countByRepositoryId(Long repositoryId);

  long countByRepositoryIdAndSuccess(Long repositoryId, Boolean success);
}
