package cloud.xcan.angus.core.repo.domain.notification;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NotificationListRepo extends CustomBaseRepository<Notification> {
    StringBuilder getSqlTemplate0(SearchMode mode, Class<Notification> mainClz,
        Set<SearchCriteria> criteria, String tableName, String... matches);
    String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params);
}
