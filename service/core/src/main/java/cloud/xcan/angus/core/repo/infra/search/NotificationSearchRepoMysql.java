package cloud.xcan.angus.core.repo.infra.search;

import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.repo.domain.notification.Notification;
import cloud.xcan.angus.core.repo.domain.notification.NotificationListRepo;
import cloud.xcan.angus.core.repo.domain.notification.NotificationSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationSearchRepoMysql extends AbstractSearchRepository<Notification>
    implements NotificationSearchRepo {

  @Resource
  private NotificationListRepo notificationListRepo;

  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria,
      Class<Notification> mainClz, Object[] objects, String... matches) {
    return notificationListRepo.getSqlTemplate0(getSearchMode(), mainClz, criteria,
        "notification", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return notificationListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return SearchMode.MATCH;
  }
}
