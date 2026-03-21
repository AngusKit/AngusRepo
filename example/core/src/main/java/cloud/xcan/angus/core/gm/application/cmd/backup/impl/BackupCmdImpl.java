package cloud.xcan.angus.core.gm.application.cmd.backup.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.PermissionCheck;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.backup.BackupCmd;
import cloud.xcan.angus.core.gm.application.cmd.backup.BackupRestoreTaskCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.application.query.backup.BackupQuery;
import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.BackupRepo;
import cloud.xcan.angus.core.gm.domain.backup.enums.BackupStatus;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.ProtocolException;
import org.springframework.util.StringUtils;
import jakarta.annotation.Resource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class BackupCmdImpl extends CommCmd<Backup, Long> implements BackupCmd {

  @Resource
  private BackupRepo backupRepo;

  @Resource
  private BackupQuery backupQuery;

  @Resource
  private BackupRestoreTaskCmd backupRestoreTaskCmd;

  @Resource
  private ApplicationQuery applicationQuery;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Backup create(Backup backup) {
    return new BizTemplate<Backup>() {
      String storagePath;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();

        // 检查指定备份应用是否存在
        if (backup.getApplicationId() != null) {
          applicationQuery.findAndCheck(backup.getApplicationId());
        }

        // 检查备份设置路径对应空间是否可用（空间剩余2GB以上）
        storagePath = backupQuery.getStoragePath();
        if (storagePath != null && !storagePath.trim().isEmpty()) {
          File storageDir = new File(storagePath.trim());
          if (!storageDir.exists()) {
            try {
              Files.createDirectories(Paths.get(storagePath.trim()));
            } catch (Exception e) {
              throw ProtocolException.of("无法创建备份存储路径: {0}", new Object[]{storagePath});
            }
          }
          if (!storageDir.isDirectory()) {
            throw ProtocolException.of("备份存储路径「{0}」不是目录", new Object[]{storagePath});
          }

          // 检查可用空间（需要至少10GB）
          long freeSpaceBytes = storageDir.getUsableSpace();
          long requiredSpaceBytes = 10L * 1024 * 1024 * 1024; // 10GB
          if (freeSpaceBytes < requiredSpaceBytes) {
            throw ProtocolException.of("备份存储路径「{0}」可用空间不足，需要至少10GB，当前可用：{1}GB",
                new Object[]{storagePath,
                    String.format("%.2f", freeSpaceBytes / (1024.0 * 1024.0 * 1024.0))});
          }
        }

        // 检查是否存在正在进行的备份任务
        List<Backup> inProgressBackups = backupRepo.findByStatus(BackupStatus.IN_PROGRESS);
        List<Backup> pendingBackups = backupRepo.findByStatus(BackupStatus.PENDING);
        if (!inProgressBackups.isEmpty() || !pendingBackups.isEmpty()) {
          throw ProtocolException.of("已有备份任务正在进行中或等待执行，请等待完成后再创建新任务");
        }
      }

      @Override
      protected Backup process() {
        // 设置成待备份状态，由 BackupJob 调度备份
        backup.setStatus(BackupStatus.PENDING);
        backup.setBackupPath(storagePath.trim());
        insert(backup);

        // 记录操作日志
        String backupName = backup.getName();
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.CONFIG,
            backup.getId(),
            backupName,
            OperationMessage.BACKUP_CREATE_DETAILS,
            new Object[]{backupName}
        );

        return backup;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void restore(Long id) {
    new BizTemplate<Void>() {
      Backup backup;

      @Override
      protected void checkParams() {
        backup = backupQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        backup.setStatus(BackupStatus.RESTORING);
        // 设置成待恢复状态，由 BackupRestoreJob 调度恢复
        backupRepo.save(backup);

        // 记录操作日志
        String backupName = backup.getName();
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            backup.getId(),
            backupName,
            OperationMessage.BACKUP_RESTORE_DETAILS,
            new Object[]{backupName}
        );

        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void runBackup(Long id) {
    new BizTemplate<Void>() {
      Backup backup;
      String storagePath;

      @Override
      protected void checkParams() {
        backup = backupQuery.findAndCheck(id);
        if (backup.getStatus() != BackupStatus.FAILED) {
          throw ProtocolException.of("仅支持重新运行失败的备份任务，当前状态：{0}",
              new Object[]{backup.getStatus().name()});
        }
        List<Backup> inProgressBackups = backupRepo.findByStatus(BackupStatus.IN_PROGRESS);
        List<Backup> pendingBackups = backupRepo.findByStatus(BackupStatus.PENDING);
        if (!inProgressBackups.isEmpty() || !pendingBackups.isEmpty()) {
          throw ProtocolException.of("已有备份任务正在进行中或等待执行，请等待完成后再重试");
        }
        storagePath = backupQuery.getStoragePath();
        if (!StringUtils.hasText(storagePath)) {
          throw ProtocolException.of("备份存储路径未配置");
        }
      }

      @Override
      protected Void process() {
        backup.setStatus(BackupStatus.PENDING);
        backup.setErrorMessage(null);
        backup.setStartTime(null);
        backup.setEndTime(null);
        backup.setFileSize(null);
        backup.setBackupPath(storagePath.trim());
        backup.setVerified(false);
        backupRepo.save(backup);

        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            backup.getId(),
            backup.getName(),
            OperationMessage.BACKUP_RUN_DETAILS,
            new Object[]{backup.getName()}
        );
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      Backup backup;

      @Override
      protected void checkParams() {
        backup = backupQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        // 保存备份名称用于操作日志（删除前获取）
        String backupName = backup.getName();

        // 删除备份记录
        backupRepo.deleteById(id);

        // 删除恢复记录
        backupRestoreTaskCmd.deleteByBackupId(id);

        // 删除备份文件（根据备份路径）
        if (backup.getBackupPath() != null && !backup.getBackupPath().trim().isEmpty()) {
          try {
            Path backupFilePath = Paths.get(backup.getBackupPath());
            if (Files.exists(backupFilePath)) {
              Files.deleteIfExists(backupFilePath);
            }
          } catch (Exception e) {
            // 记录日志但不抛出异常，避免影响删除操作
            log.error("删除备份异常", e);
          }
        }

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.CONFIG,
            id,
            backupName,
            OperationMessage.BACKUP_DELETE_DETAILS,
            new Object[]{backupName}
        );

        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Backup, Long> getRepository() {
    return backupRepo;
  }
}
