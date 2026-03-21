package cloud.xcan.angus.core.gm.application.cmd.backup;

import cloud.xcan.angus.core.gm.domain.backup.RestoreTask;

public interface BackupRestoreTaskCmd {

  /**
   * 创建恢复任务
   */
  RestoreTask create(RestoreTask restoreTask);

  /**
   * 根据备份ID删除恢复任务
   */
  void deleteByBackupId(Long backupId);
}
