package cloud.xcan.angus.core.gm.domain.backup;

import lombok.Getter;
import lombok.Setter;

/**
 * 恢复选项
 */
@Getter
@Setter
public class RestoreOptions {

  /**
   * 是否恢复数据库
   */
  private Boolean restoreDatabase = true;

  /**
   * 是否恢复配置文件
   */
  private Boolean restoreConfig = true;

  /**
   * 是否恢复文件数据
   */
  private Boolean restoreFiles = true;

  /**
   * 是否恢复系统日志
   */
  private Boolean restoreLogs = false;
}
