package cloud.xcan.angus.core.gm.interfaces.backup.facade;

import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupValidateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.RestoreCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.RestoreFindDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupValidationVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.RestoreTaskDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.RestoreTaskListVo;
import cloud.xcan.angus.remote.PageResult;

public interface RestoreTaskFacade {

  /**
   * 创建恢复任务
   */
  RestoreTaskDetailVo create(RestoreCreateDto dto);

  /**
   * 查询恢复任务详情
   */
  RestoreTaskDetailVo getDetail(Long id);

  /**
   * 查询恢复任务列表
   */
  PageResult<RestoreTaskListVo> list(RestoreFindDto dto);

  /**
   * 验证备份文件
   */
  BackupValidationVo validate(BackupValidateDto dto);
}
