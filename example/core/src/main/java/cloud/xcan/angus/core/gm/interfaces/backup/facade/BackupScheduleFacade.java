package cloud.xcan.angus.core.gm.interfaces.backup.facade;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.ScheduleCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.ScheduleUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.ScheduleDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.ScheduleRunVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.ScheduleStatusVo;
import java.util.List;

/**
 * 备份计划门面接口
 */
public interface BackupScheduleFacade {

  /**
   * 创建备份计划
   */
  ScheduleDetailVo createSchedule(ScheduleCreateDto dto);

  /**
   * 更新备份计划
   */
  ScheduleDetailVo updateSchedule(Long id, ScheduleUpdateDto dto);

  /**
   * 更新备份计划状态
   */
  ScheduleStatusVo updateScheduleStatus(Long id, EnabledStatusUpdateDto dto);

  /**
   * 删除备份计划
   */
  void deleteSchedule(Long id);

  /**
   * 立即执行备份计划
   */
  ScheduleRunVo runSchedule(Long id);

  /**
   * 获取备份计划列表
   */
  List<ScheduleDetailVo> listSchedules();

}
