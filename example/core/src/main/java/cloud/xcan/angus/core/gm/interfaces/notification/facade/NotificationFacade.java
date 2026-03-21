package cloud.xcan.angus.core.gm.interfaces.notification.facade;

import cloud.xcan.angus.core.gm.interfaces.notification.facade.dto.NotificationArchiveDto;
import cloud.xcan.angus.core.gm.interfaces.notification.facade.dto.NotificationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.notification.facade.dto.NotificationDeleteDto;
import cloud.xcan.angus.core.gm.interfaces.notification.facade.dto.NotificationQueryDto;
import cloud.xcan.angus.core.gm.interfaces.notification.facade.dto.NotificationReadStatusDto;
import cloud.xcan.angus.core.gm.interfaces.notification.facade.dto.NotificationStarStatusDto;
import cloud.xcan.angus.core.gm.interfaces.notification.facade.dto.NotificationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.notification.facade.vo.BatchOperationResultVo;
import cloud.xcan.angus.core.gm.interfaces.notification.facade.vo.NotificationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.notification.facade.vo.NotificationStatisticsVo;
import cloud.xcan.angus.remote.PageResult;

/**
 * 通知门面服务接口
 */
public interface NotificationFacade {

  /**
   * 创建通知
   */
  NotificationDetailVo create(NotificationCreateDto dto);

  /**
   * 更新通知
   */
  NotificationDetailVo update(Long id, NotificationUpdateDto dto);

  /**
   * 标记通知已读/未读
   */
  BatchOperationResultVo updateReadStatus(NotificationReadStatusDto dto);

  /**
   * 标记通知星标状态
   */
  BatchOperationResultVo updateStarredStatus(NotificationStarStatusDto dto);

  /**
   * 归档通知
   */
  BatchOperationResultVo archive(NotificationArchiveDto dto);

  /**
   * 批量标记已读
   */
  BatchOperationResultVo markAllAsRead();

  /**
   * 删除通知
   */
  void delete(NotificationDeleteDto dto);

  /**
   * 查询通知详情
   */
  NotificationDetailVo getDetail(Long id);

  /**
   * 查询通知列表（分页）
   */
  PageResult<NotificationDetailVo> list(NotificationQueryDto dto);

  /**
   * 查询通知统计
   */
  NotificationStatisticsVo getStatistics();
}

