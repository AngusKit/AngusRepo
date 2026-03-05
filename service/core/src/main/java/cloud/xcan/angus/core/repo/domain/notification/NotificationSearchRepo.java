package cloud.xcan.angus.core.repo.domain.notification;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NotificationSearchRepo extends CustomBaseRepository<Notification> {
}
