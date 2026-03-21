package cloud.xcan.angus.core.gm.application.cmd.notification;

import cloud.xcan.angus.core.gm.domain.notification.Notification;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationPriority;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationType;
import java.util.List;

/**
 * 通知业务工具命令服务接口
 * <p>
 * 提供便捷的业务写入通知方法，简化业务代码中的通知创建逻辑
 * </p>
 *
 * @author Angus
 */
public interface NotificationHelperCmd {

  // ==================== 基础创建方法 ====================

  /**
   * 创建通知（完整参数）
   *
   * @param type         通知类型
   * @param title        标题
   * @param description  描述
   * @param category     分类
   * @param priority     优先级
   * @param targetUserId 目标用户ID（null表示发送给所有用户）
   * @return 创建的通知
   */
  Notification create(NotificationType type, String title, String description,
      String category, NotificationPriority priority, Long targetUserId);

  /**
   * 创建通知（默认中等优先级）
   *
   * @param type         通知类型
   * @param title        标题
   * @param description  描述
   * @param category     分类
   * @param targetUserId 目标用户ID（null表示发送给所有用户）
   * @return 创建的通知
   */
  Notification create(NotificationType type, String title, String description,
      String category, Long targetUserId);

  /**
   * 创建系统通知（发送给所有用户）
   *
   * @param type        通知类型
   * @param title       标题
   * @param description 描述
   * @param category    分类
   * @param priority    优先级
   * @return 创建的通知
   */
  Notification createSystemNotification(NotificationType type, String title,
      String description, String category, NotificationPriority priority);

  // ==================== 成功类型通知 ====================

  /**
   * 创建成功通知
   *
   * @param title        标题
   * @param description  描述
   * @param category     分类
   * @param priority     优先级
   * @param targetUserId 目标用户ID（null表示发送给所有用户）
   * @return 创建的通知
   */
  Notification createSuccess(String title, String description, String category,
      NotificationPriority priority, Long targetUserId);

  // ==================== 信息类型通知 ====================

  /**
   * 创建信息通知
   *
   * @param title        标题
   * @param description  描述
   * @param category     分类
   * @param priority     优先级
   * @param targetUserId 目标用户ID（null表示发送给所有用户）
   * @return 创建的通知
   */
  Notification createInfo(String title, String description, String category,
      NotificationPriority priority, Long targetUserId);

  // ==================== 批量创建方法 ====================

  /**
   * 批量创建通知（发送给多个用户）
   *
   * @param type          通知类型
   * @param title         标题
   * @param description   描述
   * @param category      分类
   * @param priority      优先级
   * @param targetUserIds 目标用户ID列表
   * @return 创建的通知列表
   */
  List<Notification> createBatch(NotificationType type, String title, String description,
      String category, NotificationPriority priority, List<Long> targetUserIds);

  // ==================== 使用消息键创建通知 ====================

  /**
   * 使用消息键创建通知（支持国际化，单用户版本）
   *
   * @param type            通知类型
   * @param titleKey        标题消息键
   * @param descriptionKey  描述消息键
   * @param category        分类
   * @param priority        优先级
   * @param targetUserId    目标用户ID（null表示发送给所有用户）
   * @param titleArgs       标题参数
   * @param descriptionArgs 描述参数
   * @return 创建的通知
   */
  Notification createByMessageKey(NotificationType type, String titleKey,
      String descriptionKey, String category, NotificationPriority priority,
      Long targetUserId, Object[] titleArgs, Object[] descriptionArgs);

  /**
   * 使用消息键创建通知（支持国际化，批量版本）
   *
   * @param type            通知类型
   * @param titleKey        标题消息键
   * @param descriptionKey  描述消息键
   * @param category        分类
   * @param priority        优先级
   * @param targetUserIds   目标用户ID列表
   * @param titleArgs       标题参数
   * @param descriptionArgs 描述参数
   * @return 创建的通知列表
   */
  List<Notification> createBatchByMessageKey(NotificationType type, String titleKey,
      String descriptionKey, String category, NotificationPriority priority,
      List<Long> targetUserIds, Object[] titleArgs, Object[] descriptionArgs);

}

