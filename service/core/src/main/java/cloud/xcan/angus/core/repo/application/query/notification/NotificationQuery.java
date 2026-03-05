package cloud.xcan.angus.core.repo.application.query.notification;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.domain.notification.Notification;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.vo.NotificationStatisticsVo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface NotificationQuery {
  Page<Notification> find(GenericSpecification<Notification> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);
  Optional<Notification> findById(String id);
  Notification findAndCheck(String id);
  NotificationStatisticsVo getStatistics();
}
