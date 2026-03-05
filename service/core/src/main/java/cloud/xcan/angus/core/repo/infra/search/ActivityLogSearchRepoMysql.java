package cloud.xcan.angus.core.repo.infra.search;

import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLog;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLogListRepo;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLogSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityLogSearchRepoMysql extends AbstractSearchRepository<ActivityLog>
    implements ActivityLogSearchRepo {

  @Resource
  private ActivityLogListRepo activityLogListRepo;

  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria,
      Class<ActivityLog> mainClz, Object[] objects, String... matches) {
    return activityLogListRepo.getSqlTemplate0(getSearchMode(), mainClz, criteria,
        "activity_log", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return activityLogListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return SearchMode.MATCH;
  }
}
