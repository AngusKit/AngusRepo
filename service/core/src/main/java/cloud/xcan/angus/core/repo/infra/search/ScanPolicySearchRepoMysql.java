package cloud.xcan.angus.core.repo.infra.search;

import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicy;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicyListRepo;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicySearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class ScanPolicySearchRepoMysql extends AbstractSearchRepository<ScanPolicy>
    implements ScanPolicySearchRepo {

  @Resource
  private ScanPolicyListRepo scanPolicyListRepo;

  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria,
      Class<ScanPolicy> mainClz, Object[] objects, String... matches) {
    return scanPolicyListRepo.getSqlTemplate0(getSearchMode(), mainClz, criteria,
        "scan_policy", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return scanPolicyListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return SearchMode.MATCH;
  }
}
