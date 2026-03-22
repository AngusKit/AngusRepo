package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.application.query.repository.RepositoryQuery;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.vo.RepositoryStatisticsVo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Stub implementation of {@link RepositoryQuery} for unit testing protocol controllers.
 * Returns a pre-configured RepoEntity or throws if not found.
 */
class StubRepositoryQuery implements RepositoryQuery {

  private RepoEntity repo;

  void setRepo(RepoEntity repo) {
    this.repo = repo;
  }

  @Override
  public RepoEntity findByNameAndCheck(String name) {
    if (repo != null && repo.getName().equals(name)) {
      return repo;
    }
    throw new IllegalArgumentException("Repository not found: " + name);
  }

  @Override
  public RepoEntity findAndCheck(Long id) {
    if (repo != null && repo.getId().equals(id)) {
      return repo;
    }
    throw new IllegalArgumentException("Repository not found: " + id);
  }

  @Override
  public Optional<RepoEntity> findById(Long id) {
    if (repo != null && repo.getId().equals(id)) {
      return Optional.of(repo);
    }
    return Optional.empty();
  }

  @Override
  public Page<RepoEntity> find(GenericSpecification<RepoEntity> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return Page.empty();
  }

  @Override
  public RepositoryStatisticsVo getStatistics() {
    return null;
  }
}
