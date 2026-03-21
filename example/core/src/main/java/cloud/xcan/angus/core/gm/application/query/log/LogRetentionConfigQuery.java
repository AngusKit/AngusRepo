package cloud.xcan.angus.core.gm.application.query.log;

import cloud.xcan.angus.api.commonlink.setting.logretention.LogRetentionConfig;
import java.util.List;

/**
 * 日志清理配置查询服务
 */
public interface LogRetentionConfigQuery {

  /**
   * 查询配置列表
   */
  List<LogRetentionConfig> findList(Long applicationId);

  List<LogRetentionConfig> findList();
}
