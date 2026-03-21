package cloud.xcan.angus.core.gm.infra.job;

import cloud.xcan.angus.core.gm.application.cmd.notification.NotificationHelperCmd;
import cloud.xcan.angus.core.gm.application.query.backup.BackupRestoreTaskQuery;
import cloud.xcan.angus.core.gm.domain.backup.RestoreTask;
import cloud.xcan.angus.core.gm.domain.backup.RestoreTaskRepo;
import cloud.xcan.angus.core.gm.domain.backup.enums.RestoreStatus;
import cloud.xcan.angus.core.gm.domain.notification.NotificationMessage;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationPriority;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationType;
import cloud.xcan.angus.core.gm.infra.backup.RestoreService;
import cloud.xcan.angus.core.job.JobTemplate;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 应用备份恢复任务
 */
@Slf4j
@Component
public class BackupRestoreJob {

  private static final String LOCK_KEY = "gm:job:BackupRestoreJob";

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private RestoreTaskRepo restoreTaskRepo;

  @Resource
  private BackupRestoreTaskQuery restoreTaskQuery;

  @Resource
  private RestoreService restoreService;

  @Resource
  private NotificationHelperCmd notificationHelperCmd;

  @Scheduled(fixedDelay = 5 * 1000, initialDelay = 5000)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 60, TimeUnit.MINUTES, () -> {
      try {
        // 处理待执行的恢复任务（IN_PROGRESS状态）
        processInProgressRestores();
      } catch (Exception e) {
        log.error("恢复任务执行失败", e);
      }
    });
  }

  /**
   * 处理正在执行的恢复任务（IN_PROGRESS状态）
   */
  @Transactional(rollbackFor = Exception.class)
  private void processInProgressRestores() {
    // 1. 查询所有状态为IN_PROGRESS的恢复任务
    List<RestoreTask> inProgressTasks = restoreTaskRepo.findByStatus(RestoreStatus.IN_PROGRESS);
    if (inProgressTasks.isEmpty()) {
      return;
    }

    // 2. 检查是否有多个正在执行的恢复任务（理论上不应该有，但为了安全起见）
    if (inProgressTasks.size() > 1) {
      log.warn("发现多个正在执行的恢复任务（{}个），只处理最早创建的任务", inProgressTasks.size());
    }

    // 3. 选择一个待执行的恢复任务（选择最早创建的）
    RestoreTask restoreTask = inProgressTasks.stream()
        .min((a, b) -> {
          if (a.getCreatedDate() == null && b.getCreatedDate() == null) {
            return 0;
          }
          if (a.getCreatedDate() == null) {
            return 1;
          }
          if (b.getCreatedDate() == null) {
            return -1;
          }
          return a.getCreatedDate().compareTo(b.getCreatedDate());
        })
        .orElse(null);

    if (restoreTask == null) {
      return;
    }

    // 获取创建恢复任务的用户ID
    Long createdBy = restoreTask.getCreatedBy();

    // 4. 执行恢复操作
    try {
      log.info("开始执行恢复任务：{}", restoreTask.getId());

      // 执行恢复操作
      restoreService.executeRestore(restoreTask);

      // 5. 恢复完成后更新状态为SUCCESS
      restoreTask.setStatus(RestoreStatus.SUCCESS);
      restoreTask.setEndTime(LocalDateTime.now());
      restoreTask.setProgress(100);
      restoreTask.setCurrentStep("恢复完成");
      if (restoreTask.getTotalSteps() != null) {
        restoreTask.setCompletedSteps(restoreTask.getTotalSteps());
      }
      restoreTaskRepo.save(restoreTask);

      log.info("恢复任务「{}」执行成功", restoreTask.getId());

      // 发送恢复成功通知
      if (createdBy != null) {
        String restoreTaskName = restoreTask.getBackupName() != null
            ? restoreTask.getBackupName()
            : "恢复任务 #" + restoreTask.getId();
        notificationHelperCmd.createByMessageKey(
            NotificationType.SUCCESS,
            NotificationMessage.RESTORE_COMPLETED_TITLE,
            NotificationMessage.RESTORE_COMPLETED_DESCRIPTION,
            NotificationMessage.CATEGORY_BACKUP_MANAGEMENT,
            NotificationPriority.MEDIUM,
            createdBy,
            new Object[]{restoreTaskName},
            new Object[]{restoreTaskName}
        );
      }
    } catch (Exception e) {
      // 6. 恢复失败后更新状态为FAILED
      restoreTask.setStatus(RestoreStatus.FAILED);
      restoreTask.setEndTime(LocalDateTime.now());
      String errorMessage = e.getMessage() != null ? e.getMessage() : "恢复执行失败";
      restoreTask.setErrorMessage(errorMessage);
      restoreTaskRepo.save(restoreTask);

      log.error("恢复任务「{}」执行失败", restoreTask.getId(), e);

      // 发送恢复失败通知
      if (createdBy != null) {
        String restoreTaskName = restoreTask.getBackupName() != null
            ? restoreTask.getBackupName()
            : "恢复任务 #" + restoreTask.getId();
        notificationHelperCmd.createByMessageKey(
            NotificationType.WARNING,
            NotificationMessage.RESTORE_FAILED_TITLE,
            NotificationMessage.RESTORE_FAILED_DESCRIPTION,
            NotificationMessage.CATEGORY_BACKUP_MANAGEMENT,
            NotificationPriority.HIGH,
            createdBy,
            new Object[]{restoreTaskName},
            new Object[]{restoreTaskName, errorMessage}
        );
      }
    }
  }

}
