package cloud.xcan.angus.core.gm.application.cmd.log;

import cloud.xcan.angus.core.gm.domain.log.SystemLog;
import java.util.List;

/**
 * 系统日志命令服务接口
 */
public interface SystemLogCmd {

  /**
   * 批量保存或更新日志文件记录
   */
  void batchSaveOrUpdate(List<SystemLog> systemLogs);

  /**
   * 删除日志文件
   */
  void delete(Long id, Boolean permanent);

  /**
   * 批量删除日志文件
   */
  void batchDelete(java.util.List<Long> ids, Boolean permanent);
}
