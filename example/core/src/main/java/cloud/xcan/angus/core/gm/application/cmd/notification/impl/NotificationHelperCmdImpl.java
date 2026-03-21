package cloud.xcan.angus.core.gm.application.cmd.notification.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.manager.TenantManager;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.gm.application.cmd.notification.NotificationCmd;
import cloud.xcan.angus.core.gm.application.cmd.notification.NotificationHelperCmd;
import cloud.xcan.angus.core.gm.domain.notification.Notification;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationPriority;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationType;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/**
 * 通知业务工具命令服务实现
 * <p>
 * 提供便捷的业务写入通知方法，简化业务代码中的通知创建逻辑
 * </p>
 *
 * @author Angus
 */
@Slf4j
@Service
public class NotificationHelperCmdImpl implements NotificationHelperCmd {

  private static final Object[] EMPTY_ARGS = new Object[0];

  @Resource
  private NotificationCmd notificationCmd;

  @Resource
  private MessageSource messageSource;

  @Resource
  private UserManager userManager;

  @Resource
  private TenantManager tenantManager;

  // ==================== 基础创建方法 ====================

  @Override
  public Notification create(NotificationType type, String title, String description,
      String category, NotificationPriority priority, Long targetUserId) {
    Long tenantId = getOptTenantId();
    Long userId = targetUserId == null || targetUserId < 1
        ? PrincipalContext.getUserId() : targetUserId;
    Notification notification = new Notification()
        .setType(type)
        .setTitle(title)
        .setDescription(description)
        .setCategory(category)
        .setPriority(priority != null ? priority : NotificationPriority.MEDIUM)
        .setTargetUserId(userId)
        .setTimestamp(LocalDateTime.now())
        .setIsRead(false)
        .setIsStarred(false)
        .setIsArchived(false);
    notification.setTenantId(userManager.getCachedTenantId(tenantId, userId));
    return notificationCmd.create(notification);
  }

  @Override
  public Notification create(NotificationType type, String title, String description,
      String category, Long targetUserId) {
    return create(type, title, description, category, NotificationPriority.MEDIUM, targetUserId);
  }

  @Override
  public Notification createSystemNotification(NotificationType type, String title,
      String description, String category, NotificationPriority priority) {
    return create(type, title, description, category, priority, null);
  }

  // ==================== 成功类型通知 ====================

  @Override
  public Notification createSuccess(String title, String description, String category,
      NotificationPriority priority, Long targetUserId) {
    return create(NotificationType.SUCCESS, title, description, category, priority, targetUserId);
  }

  // ==================== 信息类型通知 ====================

  @Override
  public Notification createInfo(String title, String description, String category,
      NotificationPriority priority, Long targetUserId) {
    return create(NotificationType.INFO, title, description, category, priority, targetUserId);
  }

  // ==================== 批量创建方法 ====================
  @Override
  public List<Notification> createBatch(NotificationType type, String title, String description,
      String category, NotificationPriority priority, List<Long> targetUserIds) {
    if (targetUserIds == null || targetUserIds.isEmpty()) {
      // 如果没有指定用户，创建系统通知
      return List.of(createSystemNotification(type, title, description, category, priority));
    }
    return targetUserIds.stream()
        .map(userId -> create(type, title, description, category, priority, userId))
        .toList();
  }

  // ==================== 使用消息键创建通知 ====================

  @Override
  public Notification createByMessageKey(NotificationType type, String titleKey,
      String descriptionKey, String category, NotificationPriority priority,
      Long targetUserId, Object[] titleArgs, Object[] descriptionArgs) {
    Long tenantId = userManager.getCachedTenantId(getOptTenantId(), targetUserId);
    ResolvedMessages messages = resolveMessages(tenantManager, messageSource,
        tenantId, titleKey, descriptionKey, titleArgs, descriptionArgs);
    return create(type, messages.title, messages.description, category, priority, targetUserId);
  }

  @Override
  public List<Notification> createBatchByMessageKey(NotificationType type, String titleKey,
      String descriptionKey, String category, NotificationPriority priority,
      List<Long> targetUserIds, Object[] titleArgs, Object[] descriptionArgs) {
    if (targetUserIds == null || targetUserIds.isEmpty()) {
      // 如果没有指定用户，创建系统通知
      ResolvedMessages messages = resolveMessages(tenantManager, messageSource,
          null, titleKey, descriptionKey, titleArgs, descriptionArgs);
      return List.of(
          createSystemNotification(type, messages.title, messages.description, category, priority));
    }
    return targetUserIds.stream()
        .map(userId -> {
          Long tenantId = userManager.getCachedTenantId(getOptTenantId(), userId);
          ResolvedMessages messages = resolveMessages(tenantManager, messageSource,
              tenantId, titleKey, descriptionKey, titleArgs, descriptionArgs);
          return create(type, messages.title, messages.description, category, priority, userId);
        })
        .toList();
  }

  /**
   * 解析国际化消息
   */
  private static ResolvedMessages resolveMessages(TenantManager tenantManager,
      MessageSource messageSource, Long tenantId, String titleKey, String descriptionKey,
      Object[] titleArgs, Object[] descriptionArgs) {
    Locale locale = tenantId != null
        ? tenantManager.getCachedDefaultLanguage(tenantId).toLocale()
        : Language.DEFAULT.toLocale();
    Object[] safeTitleArgs = titleArgs != null ? titleArgs : EMPTY_ARGS;
    Object[] safeDescArgs = descriptionArgs != null ? descriptionArgs : EMPTY_ARGS;
    String title = messageSource.getMessage(titleKey, safeTitleArgs, titleKey, locale);
    String description = messageSource.getMessage(descriptionKey, safeDescArgs, descriptionKey,
        locale);
    return new ResolvedMessages(title, description);
  }

  private record ResolvedMessages(String title, String description) {

  }
}

