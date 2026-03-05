package cloud.xcan.angus.core.repo.infra.persistence.postgres.notification;

import cloud.xcan.angus.core.repo.domain.notification.NotificationRepo;
import org.springframework.stereotype.Repository;

@Repository("notificationRepo")
public interface NotificationRepoPostgres extends NotificationRepo {
}
