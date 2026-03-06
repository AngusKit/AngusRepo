package cloud.xcan.angus.core.repo.application.query.repository;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.vo.RepositoryStatisticsVo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface RepositoryQuery {

  Page<RepoEntity> find(GenericSpecification<RepoEntity> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  Optional<RepoEntity> findById(Long id);

  RepoEntity findAndCheck(Long id);

  RepoEntity findByNameAndCheck(String name);

  RepositoryStatisticsVo getStatistics();
}
