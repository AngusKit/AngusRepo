package cloud.xcan.angus.core.repo.infra.persistence.mysql.notification;

import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.repo.domain.notification.Notification;
import cloud.xcan.angus.core.repo.domain.notification.NotificationListRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationListRepoMysql extends AbstractSearchRepository<Notification>
    implements NotificationListRepo {

  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria,
      Class<Notification> mainClz, Object[] objects, String... matches) {
    return getSqlTemplate0(getSearchMode(), mainClz, criteria, "notification", matches);
  }

  @Override
  public StringBuilder getSqlTemplate0(SearchMode mode, Class<Notification> mainClz,
      Set<SearchCriteria> criteria, String tableName, String... matches) {
    String mainAlis = "a";
    StringBuilder sql = new StringBuilder(
        "SELECT %s FROM " + tableName + " " + mainAlis + " WHERE 1=1 ");
    sql.append(getCriteriaAliasCondition(criteria, mainClz, mainAlis, mode, false, matches));
    return sql;
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return "a.*";
  }
}
