package cloud.xcan.angus.core.repo.interfaces.notification.facade;

import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationBatchDeleteDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationBatchReadDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationCreateDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationFindDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.vo.NotificationDetailVo;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.vo.NotificationStatisticsVo;
import cloud.xcan.angus.remote.PageResult;

public interface NotificationFacade {
  NotificationDetailVo create(NotificationCreateDto dto);
  NotificationDetailVo update(String id, NotificationUpdateDto dto);
  void markAsRead(String id);
  void delete(String id);
  NotificationDetailVo getById(String id);
  PageResult<NotificationDetailVo> list(NotificationFindDto dto);
  NotificationStatisticsVo getStatistics();
  void markBatchAsRead(NotificationBatchReadDto dto);
  void deleteBatch(NotificationBatchDeleteDto dto);
  void addStar(String id);
  void removeStar(String id);
  void archive(String id);
  void unarchive(String id);
}
