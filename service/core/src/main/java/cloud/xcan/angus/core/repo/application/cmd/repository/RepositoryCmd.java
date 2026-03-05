package cloud.xcan.angus.core.repo.application.cmd.repository;

import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryStatus;
import java.util.List;

public interface RepositoryCmd {

  RepoEntity create(RepoEntity repository);

  RepoEntity update(RepoEntity repository);

  RepoEntity updateStatus(Long id, RepositoryStatus status);

  void delete(Long id);

  void deleteBatch(List<Long> ids);
}
