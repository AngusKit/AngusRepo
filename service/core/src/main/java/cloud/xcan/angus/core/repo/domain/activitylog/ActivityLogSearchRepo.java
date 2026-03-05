package cloud.xcan.angus.core.repo.domain.activitylog;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ActivityLogSearchRepo extends CustomBaseRepository<ActivityLog> {

}
