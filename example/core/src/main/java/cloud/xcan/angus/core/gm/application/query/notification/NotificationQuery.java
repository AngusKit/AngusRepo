package cloud.xcan.angus.core.gm.application.query.notification;

import cloud.xcan.angus.core.gm.domain.notification.Notification;
import cloud.xcan.angus.core.gm.interfaces.notification.facade.vo.NotificationStatisticsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 通知查询服务接口
 */
public interface NotificationQuery {

  /**
   * 查询通知详情
   */
  Notification detail(Long id);

  /**
   * 根据ID查询通知（带校验）
   */
  Notification findAndCheck(Long id);

  /**
   * 通用分页查询
   */
  Page<Notification> list(GenericSpecification<Notification> spec, Pageable pageable,
      boolean fullTextSearch, String[] match);

  /**
   * 查询通知统计
   */
  NotificationStatisticsVo getStatistics();

  /**
   * 根据时间范围查询
   */
  List<Notification> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
}

