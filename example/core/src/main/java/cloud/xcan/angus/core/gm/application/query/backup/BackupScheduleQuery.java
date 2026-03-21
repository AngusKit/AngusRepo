package cloud.xcan.angus.core.gm.application.query.backup;

import cloud.xcan.angus.core.gm.domain.backup.BackupSchedule;
import java.util.List;

/**
 * 备份计划查询服务接口
 */
public interface BackupScheduleQuery {

  /**
   * 根据ID查找备份计划并检查是否存在
   */
  BackupSchedule findAndCheck(Long id);

  /**
   * 检查备份计划名称是否存在
   */
  boolean existsByName(String name);

  /**
   * 查找所有备份计划
   */
  List<BackupSchedule> findAll();
}
