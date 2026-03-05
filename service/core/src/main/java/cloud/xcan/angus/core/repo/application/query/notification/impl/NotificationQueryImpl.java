package cloud.xcan.angus.core.repo.application.query.notification.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.application.query.notification.NotificationQuery;
import cloud.xcan.angus.core.repo.domain.notification.Notification;
import cloud.xcan.angus.core.repo.domain.notification.NotificationListRepo;
import cloud.xcan.angus.core.repo.domain.notification.NotificationRepo;
import cloud.xcan.angus.core.repo.domain.notification.NotificationSearchRepo;
import cloud.xcan.angus.core.repo.domain.notification.NotificationType;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.vo.NotificationStatisticsVo;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class NotificationQueryImpl implements NotificationQuery {

  @Resource
  private NotificationRepo notificationRepo;

  @Resource
  private NotificationListRepo notificationListRepo;

  @Resource
  private NotificationSearchRepo notificationSearchRepo;

  @Override
  public Page<Notification> find(GenericSpecification<Notification> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Notification>>() {
      @Override
      protected Page<Notification> process() {
        return fullTextSearch
            ? notificationSearchRepo.find(spec.getCriteria(), pageable, Notification.class, match)
            : notificationListRepo.find(spec.getCriteria(), pageable, Notification.class, null);
      }
    }.execute();
  }

  @Override
  public Optional<Notification> findById(String id) {
    return notificationRepo.findById(id);
  }

  @Override
  public Notification findAndCheck(String id) {
    return notificationRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("通知不存在: " + id));
  }

  @Override
  public NotificationStatisticsVo getStatistics() {
    return new BizTemplate<NotificationStatisticsVo>() {
      @Override
      protected NotificationStatisticsVo process() {
        String tenantId = PrincipalContext.getTenantId();
        Long userId = PrincipalContext.getUserId();
        NotificationStatisticsVo stats = new NotificationStatisticsVo();
        stats.setTotalCount(notificationRepo.countTotal(tenantId, userId));
        stats.setUnreadCount(notificationRepo.countUnread(tenantId, userId));
        stats.setSecurityCount(notificationRepo.countByType(tenantId, userId, NotificationType.SECURITY));
        stats.setStorageCount(notificationRepo.countByType(tenantId, userId, NotificationType.STORAGE));
        stats.setAccessCount(notificationRepo.countByType(tenantId, userId, NotificationType.ACCESS));
        stats.setArtifactCount(notificationRepo.countByType(tenantId, userId, NotificationType.ARTIFACT));
        stats.setSystemCount(notificationRepo.countByType(tenantId, userId, NotificationType.SYSTEM));
        return stats;
      }
    }.execute();
  }
}
