package cloud.xcan.angus.core.repo.infra.search;

import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicy;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicyListRepo;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicySearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class CleanupPolicySearchRepoMysql extends AbstractSearchRepository<CleanupPolicy>
    implements CleanupPolicySearchRepo {

  @Resource
  private CleanupPolicyListRepo cleanupPolicyListRepo;

  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria,
      Class<CleanupPolicy> mainClz, Object[] objects, String... matches) {
    return cleanupPolicyListRepo.getSqlTemplate0(getSearchMode(), mainClz, criteria,
        "cleanup_policy", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return cleanupPolicyListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return SearchMode.MATCH;
  }
}
