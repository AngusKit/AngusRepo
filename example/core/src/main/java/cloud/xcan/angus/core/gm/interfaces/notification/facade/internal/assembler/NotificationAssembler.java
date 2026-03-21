package cloud.xcan.angus.core.gm.interfaces.notification.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.notification.Notification;
import cloud.xcan.angus.core.gm.interfaces.notification.facade.dto.NotificationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.notification.facade.dto.NotificationQueryDto;
import cloud.xcan.angus.core.gm.interfaces.notification.facade.dto.NotificationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.notification.facade.vo.NotificationDetailVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 通知数据组装器
 */
public class NotificationAssembler {

  public static Notification toCreateDomain(NotificationCreateDto dto) {
    Notification notification = new Notification();
    notification.setType(dto.getType());
    notification.setTitle(dto.getTitle());
    notification.setDescription(dto.getDescription());
    notification.setCategory(dto.getCategory());
    notification.setPriority(dto.getPriority());
    notification.setTimestamp(LocalDateTime.now());

    // 设置目标用户ID（如果指定了多个用户，这里简化处理，只取第一个）
    // 实际应该创建多条记录或使用关联表
    if (dto.getTargetUserIds() != null && !dto.getTargetUserIds().isEmpty()) {
      notification.setTargetUserId(dto.getTargetUserIds().get(0));
    }

    return notification;
  }

  public static Notification toUpdateDomain(Long id, NotificationUpdateDto dto) {
    Notification notification = new Notification();
    notification.setId(id);
    notification.setTitle(dto.getTitle());
    notification.setDescription(dto.getDescription());
    notification.setCategory(dto.getCategory());
    notification.setPriority(dto.getPriority());
    return notification;
  }

  public static NotificationDetailVo toDetailVo(Notification notification) {
    NotificationDetailVo vo = new NotificationDetailVo();
    vo.setId(notification.getId());
    vo.setType(notification.getType());
    vo.setTitle(notification.getTitle());
    vo.setDescription(notification.getDescription());
    vo.setCategory(notification.getCategory());
    vo.setIsRead(notification.getIsRead());
    vo.setIsStarred(notification.getIsStarred());
    vo.setIsArchived(notification.getIsArchived());
    vo.setPriority(notification.getPriority());
    vo.setTimestamp(notification.getTimestamp());
    return vo;
  }

  public static GenericSpecification<Notification> getSpecification(NotificationQueryDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "timestamp")
        .matchSearchFields("title", "description", "category")
        .orderByFields("id", "timestamp", "priority", "isRead")
        .inAndNotFields("isRead", "isStarred", "isArchived", "type", "priority")
        .build();

    return new GenericSpecification<>(filters);
  }
}

