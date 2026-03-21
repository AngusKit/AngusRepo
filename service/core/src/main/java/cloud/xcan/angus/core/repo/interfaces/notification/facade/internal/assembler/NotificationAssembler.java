package cloud.xcan.angus.core.repo.interfaces.notification.facade.internal.assembler;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.repo.domain.notification.Notification;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationCreateDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationFindDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.vo.NotificationDetailVo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class NotificationAssembler {

  public static Notification toCreateEntity(NotificationCreateDto dto) {
    Notification entity = new Notification();
    entity.setTitle(dto.getTitle());
    entity.setMessage(dto.getMessage());
    entity.setType(dto.getType());
    entity.setPriority(dto.getPriority());
    entity.setTargetUserId(dto.getTargetUserId());
    entity.setSourceId(dto.getSourceId());
    entity.setSourceType(dto.getSourceType());
    entity.setActionUrl(dto.getActionUrl());
    return entity;
  }

  public static Notification toUpdateEntity(NotificationUpdateDto dto, String id) {
    Notification entity = new Notification();
    entity.setId(id);
    entity.setTitle(dto.getTitle());
    entity.setMessage(dto.getMessage());
    entity.setType(dto.getType());
    entity.setPriority(dto.getPriority());
    return entity;
  }

  public static NotificationDetailVo toDetailVo(Notification entity) {
    if (entity == null) return null;
    NotificationDetailVo vo = new NotificationDetailVo();
    vo.setId(entity.getId());
    vo.setTitle(entity.getTitle());
    vo.setMessage(entity.getMessage());
    vo.setType(entity.getType());
    vo.setPriority(entity.getPriority());
    vo.setIsRead(entity.getIsRead());
    vo.setIsStarred(entity.getIsStarred());
    vo.setIsArchived(entity.getIsArchived());
    vo.setTargetUserId(entity.getTargetUserId());
    vo.setSourceId(entity.getSourceId());
    vo.setSourceType(entity.getSourceType());
    vo.setActionUrl(entity.getActionUrl());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    vo.setReadDate(entity.getReadDate());
    return vo;
  }

  public static GenericSpecification<Notification> getSpecification(NotificationFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("title", "message")
        .orderByFields("id", "createdDate", "type", "priority", "isRead")
        .inAndNotFields("type", "priority", "isRead", "isStarred", "isArchived")
        .build();
    return new GenericSpecification<>(filters);
  }
}
