package cloud.xcan.angus.core.gm.application.cmd.backup;

import cloud.xcan.angus.core.gm.domain.backup.Backup;

public interface BackupCmd {

  /**
   * 创建备份任务
   */
  Backup create(Backup backup);

  /**
   * 恢复备份任务
   */
  void restore(Long id);

  /**
   * 删除备份任务
   */
  void delete(Long id);

  /**
   * 重新运行失败的备份任务，将状态重置为 PENDING 由 BackupJob 调度执行
   */
  void runBackup(Long id);

}
