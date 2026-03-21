package cloud.xcan.angus.core.gm.application.cmd.log;

import cloud.xcan.angus.api.commonlink.setting.logretention.LogRetentionCleanupResult;
import cloud.xcan.angus.api.commonlink.setting.logretention.LogRetentionConfig;
import java.util.List;

/**
 * 日志清理配置命令服务
 */
public interface LogRetentionConfigCmd {

  /**
   * 更新配置
   */
  LogRetentionConfig update(LogRetentionConfig config);

  /**
   * 批量更新配置
   */
  List<LogRetentionConfig> batchUpdate(List<LogRetentionConfig> configs);

  /**
   * 执行清理
   */
  LogRetentionCleanupResult cleanup(Long applicationId, Boolean dryRun);

  /**
   * 执行清理
   */
  void cleanupLogs(LogRetentionConfig config, LogRetentionCleanupResult result);
}
