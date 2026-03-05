package cloud.xcan.angus.core.repo.application.cmd.activitylog;

import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLog;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动日志命令接口
 */
public interface ActivityLogCmd {

  /**
   * 创建活动日志
   */
  ActivityLog create(ActivityLog activityLog);

  /**
   * 删除活动日志
   */
  void delete(String id);

  /**
   * 批量删除活动日志
   */
  void deleteBatch(List<String> ids);

  /**
   * 按条件批量删除活动日志
   */
  void deleteByCondition(LocalDateTime beforeDate, String category);
}
