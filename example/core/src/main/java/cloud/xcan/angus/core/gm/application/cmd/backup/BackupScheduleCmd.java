package cloud.xcan.angus.core.gm.application.cmd.backup;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.backup.BackupSchedule;

public interface BackupScheduleCmd {

  /**
   * 创建备份计划
   */
  BackupSchedule create(BackupSchedule schedule);

  /**
   * 更新备份计划
   */
  BackupSchedule update(BackupSchedule schedule);

  /**
   * 更新备份计划状态
   */
  void updateStatus(Long id, EnabledStatus status);

  /**
   * 删除备份计划
   */
  void delete(Long id);
}
