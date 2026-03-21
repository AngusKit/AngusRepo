package cloud.xcan.angus.core.gm.application.cmd.notification;

import cloud.xcan.angus.core.gm.domain.notification.Notification;
import java.util.List;

/**
 * 通知命令服务接口
 */
public interface NotificationCmd {

  /**
   * 创建通知
   */
  Notification create(Notification notification);

  /**
   * 更新通知
   */
  Notification update(Long id, Notification notification);

  /**
   * 批量更新已读状态
   */
  void updateReadStatus(List<Long> ids, Boolean isRead);

  /**
   * 批量更新星标状态
   */
  void updateStarredStatus(List<Long> ids, Boolean isStarred);

  /**
   * 批量归档
   */
  void archive(List<Long> ids);

  /**
   * 标记所有未读为已读
   */
  int markAllAsRead();

  /**
   * 删除通知
   */
  void delete(List<Long> ids);
}

