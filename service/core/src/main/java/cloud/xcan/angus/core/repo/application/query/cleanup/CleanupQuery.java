package cloud.xcan.angus.core.repo.application.query.cleanup;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupExecution;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicy;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo.CleanupStatisticsVo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CleanupQuery {

  Page<CleanupPolicy> find(GenericSpecification<CleanupPolicy> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  Optional<CleanupPolicy> findPolicyById(String id);

  CleanupPolicy findPolicyAndCheck(String id);

  List<CleanupPolicy> findPoliciesByRepositoryId(String repositoryId);

  List<CleanupExecution> findExecutionsByPolicyId(String policyId);

  Optional<CleanupExecution> findExecutionById(String id);

  CleanupStatisticsVo getStatistics();
}
