package cloud.xcan.angus.core.gm.infra.job;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.application.cmd.backup.BackupCmd;
import cloud.xcan.angus.core.gm.application.cmd.notification.NotificationHelperCmd;
import cloud.xcan.angus.core.gm.application.query.backup.BackupQuery;
import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.BackupRepo;
import cloud.xcan.angus.core.gm.domain.backup.BackupSchedule;
import cloud.xcan.angus.core.gm.domain.backup.ScheduleNextRunCalculator;
import cloud.xcan.angus.core.gm.domain.backup.BackupScheduleRepo;
import cloud.xcan.angus.core.gm.domain.backup.enums.BackupStatus;
import cloud.xcan.angus.core.gm.domain.notification.NotificationMessage;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationPriority;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationType;
import cloud.xcan.angus.core.gm.infra.backup.BackupService;
import cloud.xcan.angus.core.gm.infra.utils.CommonUtils;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler.ScheduleAssembler;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler.BackupAssembler;
import cloud.xcan.angus.core.job.JobTemplate;
import jakarta.annotation.Resource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 应用备份与检查任务（包含手动创建备份任务和按照调度时间创建任务）
 */
@Slf4j
@Component
public class BackupJob {

  private static final String LOCK_KEY = "gm:job:BackupJob";

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private BackupRepo backupRepo;

  @Resource
  private BackupQuery backupQuery;

  @Resource
  private BackupCmd backupCmd;

  @Resource
  private BackupScheduleRepo scheduleRepo;

  @Resource
  private BackupService backupService;

  @Resource
  private BackupJob self;

  @Resource
  private NotificationHelperCmd notificationHelperCmd;

  @Scheduled(fixedDelay = 30 * 1000, initialDelay = 5000)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 60, TimeUnit.MINUTES, () -> {
      try {
        // 第一部分：处理待执行的备份任务（PENDING状态）
        self.processPendingBackups();

        // 第二部分：检查并创建计划备份任务
        self.checkAndCreateScheduledBackups();

        // 第三部分：清理过期备份
        self.cleanupExpiredBackups();
      } catch (Exception e) {
        log.error("备份任务执行失败", e);
      }
    });
  }

  /**
   * 处理待执行的备份任务（PENDING状态）
   */
  @Transactional(rollbackFor = Exception.class)
  public void processPendingBackups() {

    // 1. 查询所有状态为PENDING的备份任务
    List<Backup> pendingBackups = backupRepo.findByStatus(BackupStatus.PENDING);
    if (pendingBackups.isEmpty()) {
      return;
    }

    // 2. 检查是否有正在执行的备份任务（IN_PROGRESS）
    List<Backup> inProgressBackups = backupRepo.findByStatus(BackupStatus.IN_PROGRESS);
    if (!inProgressBackups.isEmpty()) {
      log.debug("已有备份任务正在执行中，跳过本次执行");
      return;
    }

    // 3. 选择一个待执行的备份任务（选择最早创建的）
    Backup backup = pendingBackups.stream()
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

    if (backup == null) {
      return;
    }
    log.info("开始执行备份任务：{}", backup.getName());

    // 4. 更新状态为IN_PROGRESS
    backup.setStatus(BackupStatus.IN_PROGRESS);
    backup.setStartTime(LocalDateTime.now());
    backupRepo.save(backup);

    // 获取创建备份的用户ID
    Long createdBy = backup.getCreatedBy();

    try {
      // 5. 执行备份操作
      executeBackup(backup);

      // 6. 备份完成后更新状态为COMPLETED
      backup.setStatus(BackupStatus.COMPLETED);
      backup.setEndTime(LocalDateTime.now());
      backup.setVerified(true);
      backupRepo.save(backup);

      log.info("备份任务「{}」执行成功", backup.getName());

      // 发送备份成功通知
      if (createdBy != null) {
        String fileSizeStr = backup.getFileSize() != null
            ? CommonUtils.formatFileSize(backup.getFileSize())
            : "未知";
        notificationHelperCmd.createByMessageKey(
            NotificationType.SUCCESS,
            NotificationMessage.BACKUP_COMPLETED_TITLE,
            NotificationMessage.BACKUP_COMPLETED_DESCRIPTION,
            NotificationMessage.CATEGORY_BACKUP_MANAGEMENT,
            NotificationPriority.MEDIUM,
            createdBy,
            new Object[]{backup.getName()},
            new Object[]{backup.getName(), fileSizeStr}
        );
      }
    } catch (Exception e) {
      // 7. 备份失败后更新状态为FAILED
      backup.setStatus(BackupStatus.FAILED);
      backup.setEndTime(LocalDateTime.now());
      String errorMessage = e.getMessage() != null ? e.getMessage() : "备份执行失败";
      backup.setErrorMessage(errorMessage);
      backupRepo.save(backup);

      log.error("备份任务「{}」执行失败", backup.getName(), e);

      // 发送备份失败通知
      if (createdBy != null) {
        notificationHelperCmd.createByMessageKey(
            NotificationType.WARNING,
            NotificationMessage.BACKUP_FAILED_TITLE,
            NotificationMessage.BACKUP_FAILED_DESCRIPTION,
            NotificationMessage.CATEGORY_BACKUP_MANAGEMENT,
            NotificationPriority.HIGH,
            createdBy,
            new Object[]{backup.getName()},
            new Object[]{backup.getName(), errorMessage}
        );
      }
    }
  }

  /**
   * 执行备份操作
   */
  private void executeBackup(Backup backup) throws Exception {
    // 获取备份存储路径
    String storagePath = backupQuery.getStoragePath();
    if (storagePath == null || storagePath.trim().isEmpty()) {
      throw new RuntimeException("备份存储路径未配置");
    }

    // 创建备份目录（如果不存在）
    File storageDir = new File(storagePath);
    if (!storageDir.exists()) {
      storageDir.mkdirs();
    }

    // 生成备份文件名（包含时间戳）
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    // 备份文件使用.zip格式
    String fileName = String.format("backup_%s_%s.zip", backup.getName(), timestamp);
    String backupFilePath = Paths.get(storagePath, fileName).toString();

    // 设置备份路径
    backup.setBackupPath(backupFilePath);

    // 执行备份操作
    backupService.executeBackup(backup);

    // 获取备份文件大小
    File backupFile = new File(backupFilePath);
    if (backupFile.exists()) {
      backup.setFileSize(backupFile.length());
      log.info("备份文件已创建：{}，大小：{} 字节", backupFilePath, backup.getFileSize());
    } else {
      throw new RuntimeException("备份文件创建失败：" + backupFilePath);
    }
  }

  /**
   * 检查并创建计划备份任务
   */
  @Transactional(rollbackFor = Exception.class)
  public void checkAndCreateScheduledBackups() {
    // 1. 查询所有启用的备份计划（status = ENABLED）
    List<BackupSchedule> enabledSchedules = scheduleRepo.findByStatus(EnabledStatus.ENABLED);
    if (enabledSchedules.isEmpty()) {
      return;
    }

    LocalDateTime now = LocalDateTime.now();

    // 2. 遍历每个备份计划
    for (BackupSchedule schedule : enabledSchedules) {
      try {
        // 检查nextRunTime是否已到或已过
        if (schedule.getNextRunTime() == null) {
          // 如果nextRunTime为空，计算并设置
          LocalDateTime nextRunTime = ScheduleNextRunCalculator.calculate(schedule, now);
          schedule.setNextRunTime(nextRunTime);
          scheduleRepo.save(schedule);
          continue;
        }

        // 如果nextRunTime已到或已过
        if (schedule.getNextRunTime().isBefore(now) || schedule.getNextRunTime().isEqual(now)) {
          // 创建新的备份任务（状态为PENDING）
          createBackupFromSchedule(schedule);

          // 更新计划的lastRunTime为当前时间
          schedule.setLastRunTime(now);

          // 计算并更新nextRunTime
          LocalDateTime nextRunTime = ScheduleNextRunCalculator.calculate(schedule, now);
          schedule.setNextRunTime(nextRunTime);

          scheduleRepo.save(schedule);

          log.info("已为备份计划「{}」创建备份任务，下次执行时间：{}", schedule.getName(), nextRunTime);
        }
      } catch (Exception e) {
        log.error("处理备份计划「{}」时发生错误", schedule.getName(), e);
      }
    }
  }

  /**
   * 根据备份计划创建备份任务
   */
  private void createBackupFromSchedule(BackupSchedule schedule) {
    String backupName = schedule.getName() + "_" + LocalDateTime.now().format(
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    String backupDescription = "由备份计划「" + schedule.getName() + "」自动创建";
    BackupCreateDto backupDto = ScheduleAssembler.toBackupCreateDto(backupName, schedule,
        backupDescription);
    Backup backup = BackupAssembler.toCreateDomain(backupDto);
    backupCmd.create(backup);
  }

  /**
   * 清理过期备份
   */
  @Transactional(rollbackFor = Exception.class)
  public void cleanupExpiredBackups() {
    // 1. 查询所有已完成的备份任务
    List<Backup> completedBackups = backupRepo.findByStatus(BackupStatus.COMPLETED);
    if (completedBackups.isEmpty()) {
      return;
    }

    LocalDateTime now = LocalDateTime.now();
    int cleanedCount = 0;

    // 2. 遍历备份任务，根据retentionDays判断是否需要删除
    for (Backup backup : completedBackups) {
      try {
        // 如果retentionDays为空，跳过
        if (backup.getRetentionDays() == null) {
          continue;
        }

        // 如果endTime为空，跳过
        if (backup.getEndTime() == null) {
          continue;
        }

        // 计算备份时间到现在的天数
        long daysSinceBackup = java.time.Duration.between(backup.getEndTime(), now).toDays();

        // 如果超过保留天数，删除备份
        if (daysSinceBackup > backup.getRetentionDays()) {
          // 删除备份文件
          if (backup.getBackupPath() != null && !backup.getBackupPath().trim().isEmpty()) {
            try {
              Path backupFilePath = Paths.get(backup.getBackupPath());
              if (Files.exists(backupFilePath)) {
                Files.deleteIfExists(backupFilePath);
                log.debug("已删除过期备份文件：{}", backup.getBackupPath());
              }
            } catch (Exception e) {
              log.warn("删除备份文件失败：{}", backup.getBackupPath(), e);
            }
          }

          // 删除备份记录
          backupRepo.deleteById(backup.getId());
          cleanedCount++;

          log.info("已清理过期备份「{}」，保留天数：{}，已过期：{}天", backup.getName(),
              backup.getRetentionDays(), daysSinceBackup);
        }
      } catch (Exception e) {
        log.error("清理备份「{}」时发生错误", backup.getName(), e);
      }
    }

    if (cleanedCount > 0) {
      log.info("本次清理了{}个过期备份", cleanedCount);
    }
  }

}
