package cloud.xcan.angus.core.gm.infra.job;

import static cloud.xcan.angus.api.commonlink.GMConstant.TEMPLATE_CODE_SYSTEM_NOTIFICATION;
import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.formatDateTime;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.commonlink.user.UserInfo;
import cloud.xcan.angus.api.manager.TenantManager;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.query.security.SecurityQuery;
import cloud.xcan.angus.core.gm.domain.notification.Notification;
import cloud.xcan.angus.core.gm.domain.notification.NotificationRepo;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationType;
import cloud.xcan.angus.core.gm.domain.security.model.SecurityNotificationConfig;
import cloud.xcan.angus.core.job.JobTemplate;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 邮件通知任务 定时查询未发送邮件通知的记录，发送邮件并更新状态
 */
@Slf4j
@Component
public class EmailNotificationJob {

  private static final String LOCK_KEY = "git:job:EmailNotificationJob";
  private static final int BATCH_SIZE = 50; // 每次处理的通知数量

  @Resource
  private NotificationRepo notificationRepo;

  @Resource
  private EmailCmd emailCmd;

  @Resource
  private UserManager userManager;

  @Resource
  private TenantManager tenantManager;

  @Resource
  private SecurityQuery securityQuery;

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private EmailNotificationJob self;

  @Scheduled(fixedDelay = 30 * 1000, initialDelay = 5000)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 10, TimeUnit.MINUTES, () -> {
      // 0. 获取安全通知配置，检查 systemLoadHighNotify 是否开启
      var security = securityQuery.getNotificationConfig();
      if (security == null
          || !(security.getConfig() instanceof SecurityNotificationConfig config)) {
        return;
      }
      if (!Boolean.TRUE.equals(config.getUserCriticalOperationNotify())) {
        return;
      }

      // 1. 查询未发送邮件通知的记录
      List<Notification> unsentNotifications = notificationRepo.findUnsentEmailNotifications(
          BATCH_SIZE);
      if (unsentNotifications.isEmpty()) {
        log.debug("没有待发送邮件通知的记录");
        return;
      }

      log.info("发送邮件通知的记录，本次处理 {} 条", unsentNotifications.size());

      // 2. 按租户分组处理
      Map<Long, List<Notification>> notificationsByTenant = unsentNotifications.stream()
          .collect(Collectors.groupingBy(Notification::getTenantId));

      for (Map.Entry<Long, List<Notification>> entry : notificationsByTenant.entrySet()) {
        Long tenantId = entry.getKey();
        List<Notification> tenantNotifications = entry.getValue();

        try {
          // 通过 self 调用确保事务代理生效
          self.processNotifications(tenantNotifications);
        } catch (Exception e) {
          log.error("处理租户 {} 的通知邮件失败", tenantId, e);
          // 即使处理失败，也要标记为已发送，避免重复处理
          markNotificationsAsSentByIds(tenantNotifications.stream()
              .map(Notification::getId)
              .collect(Collectors.toList()));
        }
      }
    });
  }

  @Transactional(rollbackFor = Exception.class)
  public void processNotifications(List<Notification> notifications) {
    // 收集所有需要查询的用户ID
    Set<Long> userIds = notifications.stream()
        .map(Notification::getTargetUserId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    if (userIds.isEmpty()) {
      log.warn("通知列表中所有记录的 targetUserId 都为空，跳过处理");
      markNotificationsAsSentByIds(notifications.stream()
          .map(Notification::getId)
          .collect(Collectors.toList()));
      return;
    }

    // 批量获取用户信息
    Map<Long, UserInfo> userInfoMap = userManager.getUserInfoMapByIds(userIds);

    // 处理每个通知
    List<Long> processedNotificationIds = new ArrayList<>();
    for (Notification notification : notifications) {
      try {
        Long targetUserId = notification.getTargetUserId();
        if (targetUserId == null) {
          log.warn("通知 targetUserId 为空，跳过: notificationId={}", notification.getId());
          processedNotificationIds.add(notification.getId());
          continue;
        }

        UserInfo userInfo = userInfoMap.get(targetUserId);
        if (userInfo == null) {
          log.warn("未找到用户信息，跳过发送邮件: notificationId={}, userId={}",
              notification.getId(), targetUserId);
          processedNotificationIds.add(notification.getId());
          continue;
        }

        String recipientEmail = userInfo.getEmail();
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
          log.warn("用户邮箱为空，跳过发送邮件: notificationId={}, userId={}",
              notification.getId(), targetUserId);
          processedNotificationIds.add(notification.getId());
          continue;
        }

        // 发送邮件（无论成功失败都要标记为已发送）
        boolean sent = sendNotificationEmail(notification, recipientEmail, userInfo);
        if (sent) {
          log.debug("通知邮件发送成功: notificationId={}, recipientEmail={}", notification.getId(),
              recipientEmail);
        } else {
          log.warn("通知邮件发送失败: notificationId={}, recipientEmail={}", notification.getId(),
              recipientEmail);
        }

        processedNotificationIds.add(notification.getId());
      } catch (Exception e) {
        log.error("处理通知邮件失败: notificationId={}", notification.getId(), e);
        // 即使异常也要标记为已发送
        processedNotificationIds.add(notification.getId());
      }
    }

    // 批量更新邮件发送状态
    if (!processedNotificationIds.isEmpty()) {
      markNotificationsAsSentByIds(processedNotificationIds);
      log.info("已处理 {} 条通知邮件，已更新发送状态", processedNotificationIds.size());
    }
  }

  /**
   * 标记通知为已发送
   */
  private void markNotificationsAsSentByIds(List<Long> notificationIds) {
    try {
      notificationRepo.updateEmailSentStatus(notificationIds, true);
    } catch (Exception e) {
      log.error("更新通知邮件发送状态失败", e);
    }
  }

  /**
   * 根据通知类型获取对应的图标
   *
   * @param type 通知类型
   * @return 图标 emoji
   */
  private String getIconByType(NotificationType type) {
    if (type == null) {
      return "🔔";
    }
    return switch (type) {
      case SUCCESS -> "✅";
      case WARNING -> "⚠️";
      case INFO -> "ℹ️";
      default -> "🔔";
    };
  }

  public boolean sendNotificationEmail(Notification notification, String recipientEmail,
      UserInfo userInfo) {
    Language language = tenantManager.getCachedDefaultLanguage(notification.getTenantId());
    try {
      // 构建模板参数
      Map<String, String> params = new HashMap<>();

      // 基本信息
      params.put("title", notification.getTitle() != null ? notification.getTitle() : "");
      String description =
          notification.getDescription() != null ? notification.getDescription() : "";
      params.put("description", description);
      params.put("content", description); // 兼容模版中的 content 变量

      // 用户信息
      if (userInfo != null) {
        String userName = userInfo.getName() != null && !userInfo.getName().isEmpty()
            ? userInfo.getName()
            : (userInfo.getUsername() != null ? userInfo.getUsername() : "");
        params.put("userName", userName);
      } else {
        params.put("userName", "");
      }

      // 分类和优先级
      if (notification.getCategory() != null) {
        params.put("category", notification.getCategory());
      } else {
        params.put("category", "");
      }

      if (notification.getPriority() != null) {
        params.put("priority", notification.getPriority().name());
      } else {
        params.put("priority", "");
      }

      // 时间格式化
      if (notification.getTimestamp() != null) {
        LocalDateTime timestamp = notification.getTimestamp();
        params.put("timestamp", timestamp.toString());
        // 格式化日期时间，根据语言设置格式
        String dateTime = formatDateTime(timestamp, language);
        params.put("dateTime", dateTime);
      } else {
        params.put("timestamp", "");
        params.put("dateTime", "");
      }

      // 根据通知类型设置图标和样式类
      if (notification.getType() != null) {
        String icon = getIconByType(notification.getType());
        params.put("icon", icon);
        params.put("type", notification.getType().name());
        params.put("typeClass", notification.getType().name().toLowerCase());
      } else {
        params.put("icon", "🔔");
        params.put("type", "");
        params.put("typeClass", "");
      }

      // 使用模板发送邮件（异步发送）
      emailCmd.sendByTemplate(TEMPLATE_CODE_SYSTEM_NOTIFICATION, language, recipientEmail,
          null, null, params, false);

      log.info("通知邮件发送成功: notificationId={}, recipientEmail={}", notification.getId(),
          recipientEmail);
      return true;
    } catch (Exception e) {
      log.error("发送通知邮件时发生异常: notificationId={}, recipientEmail={}",
          notification.getId(), recipientEmail, e);
      return false;
    }
  }
}
