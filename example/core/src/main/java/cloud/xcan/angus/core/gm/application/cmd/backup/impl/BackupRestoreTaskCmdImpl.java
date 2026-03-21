package cloud.xcan.angus.core.gm.application.cmd.backup.impl;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.PermissionCheck;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.backup.BackupRestoreTaskCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthenticationUserQuery;
import cloud.xcan.angus.core.gm.application.query.backup.BackupQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.RestoreTask;
import cloud.xcan.angus.core.gm.domain.backup.RestoreTaskRepo;
import cloud.xcan.angus.core.gm.domain.backup.enums.BackupStatus;
import cloud.xcan.angus.core.gm.domain.backup.enums.RestoreSource;
import cloud.xcan.angus.core.gm.domain.backup.enums.RestoreStatus;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BackupRestoreTaskCmdImpl extends CommCmd<RestoreTask, Long> implements
    BackupRestoreTaskCmd {

  @Resource
  private RestoreTaskRepo restoreTaskRepo;

  @Resource
  private BackupQuery backupQuery;

  @Resource
  private UserQuery userQuery;

  @Resource
  private AuthenticationUserQuery authenticationUserQuery;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public RestoreTask create(RestoreTask restoreTask) {
    return new BizTemplate<RestoreTask>() {
      Backup backup;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();

        // 1. 校验必须是管理员且密码正确
        Long currentUserId = PrincipalContext.getUserId();
        if (currentUserId == null) {
          throw ProtocolException.of("无法获取当前用户信息，请先登录");
        }

        // 检查用户是否存在且为管理员
        User currentUser = userQuery.findAndCheck(currentUserId);
        if (currentUser.getSysAdmin() == null || !currentUser.getSysAdmin()) {
          throw ProtocolException.of("只有管理员才能执行恢复操作");
        }

        // 验证密码
        if (restoreTask.getPassword() == null || restoreTask.getPassword().trim().isEmpty()) {
          throw ProtocolException.of("管理员密码不能为空");
        }

        try {
          // 验证管理员密码
          authenticationUserQuery.checkPassword(currentUserId, restoreTask.getPassword());
        } catch (Exception e) {
          // 密码验证失败，抛出友好的错误信息
          throw ProtocolException.of("管理员密码验证失败，请检查密码是否正确");
        }

        // 2. 验证恢复源
        if (restoreTask.getSource() == RestoreSource.BACKUP) {
          if (restoreTask.getBackupId() == null) {
            throw ProtocolException.of("从备份列表恢复时，备份ID不能为空");
          }
          // 验证备份是否存在且已完成
          backup = backupQuery.findAndCheck(restoreTask.getBackupId());
          if (backup.getStatus() != BackupStatus.COMPLETED) {
            throw ProtocolException.of("备份「{0}」状态为{1}，无法进行恢复",
                new Object[]{backup.getName(), backup.getStatus()});
          }
          restoreTask.setBackupId(backup.getId());
        } else if (restoreTask.getSource() == RestoreSource.FILE_PATH) {
          if (restoreTask.getFilePath() == null || restoreTask.getFilePath().trim().isEmpty()) {
            throw ProtocolException.of("从文件路径恢复时，文件路径不能为空");
          }
          // 验证文件是否存在
          Path filePath = Paths.get(restoreTask.getFilePath());
          if (!Files.exists(filePath)) {
            throw ProtocolException.of("备份文件「{0}」不存在",
                new Object[]{restoreTask.getFilePath()});
          }
          if (!Files.isRegularFile(filePath)) {
            throw ProtocolException.of("备份文件「{0}」不是普通文件",
                new Object[]{restoreTask.getFilePath()});
          }
          if (!Files.isReadable(filePath)) {
            throw ProtocolException.of("备份文件「{0}」不可读",
                new Object[]{restoreTask.getFilePath()});
          }
        }

        // 验证恢复选项
        if (restoreTask.getOptions() == null) {
          throw ProtocolException.of("恢复选项不能为空");
        }
        if (restoreTask.getOptions().getRestoreDatabase() == null
            && restoreTask.getOptions().getRestoreConfig() == null
            && restoreTask.getOptions().getRestoreFiles() == null
            && restoreTask.getOptions().getRestoreLogs() == null) {
          throw ProtocolException.of("至少需要选择一个恢复选项");
        }

        // 检查是否有正在进行的恢复任务
        RestoreStatus inProgressStatus = RestoreStatus.IN_PROGRESS;
        long inProgressCount = restoreTaskRepo.findByStatus(inProgressStatus).size();
        if (inProgressCount > 0) {
          throw ProtocolException.of("已有恢复任务正在进行中，请等待完成后再创建新任务");
        }
      }

      @Override
      protected RestoreTask process() {
        // 设置初始状态
        restoreTask.setStatus(RestoreStatus.IN_PROGRESS);
        restoreTask.setProgress(0);
        restoreTask.setStartTime(LocalDateTime.now());
        restoreTask.setTotalSteps(4); // 默认4个步骤
        restoreTask.setCompletedSteps(0);

        // 设置备份名称（如果从备份恢复）
        if (backup != null) {
          restoreTask.setBackupName(backup.getName());
        }

        // 保存恢复任务
        insert(restoreTask);

        // 记录操作日志
        String taskName = restoreTask.getBackupName();
        String sourceName = restoreTask.getSource() == RestoreSource.BACKUP
            ? "备份列表" : "文件路径";
        BackupRestoreTaskCmdImpl.this.userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.CONFIG,
            restoreTask.getId(),
            taskName,
            OperationMessage.RESTORE_TASK_CREATE_DETAILS,
            new Object[]{taskName, sourceName}
        );

        // TODO: 异步执行恢复任务
        // restoreTaskExecutor.execute(restoreTask);
        return restoreTask;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteByBackupId(Long backupId) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        List<RestoreTask> restoreTasks = restoreTaskRepo.findByBackupId(backupId);
        if (!restoreTasks.isEmpty()) {
          restoreTaskRepo.deleteAll(restoreTasks);
        }
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<RestoreTask, Long> getRepository() {
    return restoreTaskRepo;
  }
}
