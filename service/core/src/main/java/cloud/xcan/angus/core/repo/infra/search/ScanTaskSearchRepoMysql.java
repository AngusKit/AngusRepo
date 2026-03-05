package cloud.xcan.angus.core.repo.infra.search;

import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.repo.domain.security.ScanTask;
import cloud.xcan.angus.core.repo.domain.security.ScanTaskListRepo;
import cloud.xcan.angus.core.repo.domain.security.ScanTaskSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class ScanTaskSearchRepoMysql extends AbstractSearchRepository<ScanTask>
    implements ScanTaskSearchRepo {

  @Resource
  private ScanTaskListRepo scanTaskListRepo;

  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria,
      Class<ScanTask> mainClz, Object[] objects, String... matches) {
    return scanTaskListRepo.getSqlTemplate0(getSearchMode(), mainClz, criteria,
        "scan_task", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return scanTaskListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return SearchMode.MATCH;
  }
}
