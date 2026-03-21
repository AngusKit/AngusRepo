package cloud.xcan.angus.core.gm.application.cmd.interfaces;

import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLog;
import java.util.List;

/**
 * API请求日志命令服务接口
 */
public interface InterfaceRequestLogCmd {

  /**
   * 批量创建日志记录
   */
  void batchCreate(List<InterfaceRequestLog> logs);
}
