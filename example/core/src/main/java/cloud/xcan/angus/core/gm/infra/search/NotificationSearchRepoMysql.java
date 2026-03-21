package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.notification.Notification;
import cloud.xcan.angus.core.gm.domain.notification.NotificationSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationSearchRepoMysql extends SimpleSearchRepository<Notification>
    implements NotificationSearchRepo {

}

