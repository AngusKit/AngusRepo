package cloud.xcan.angus.core.gm.application.cmd.system;

import cloud.xcan.angus.core.gm.domain.system.AlertRecord;
import java.util.List;

/**
 * 告警记录命令服务接口
 */
public interface AlertRecordCmd {

  /**
   * 创建告警记录
   */
  AlertRecord create(AlertRecord alertRecord);

  /**
   * 更新告警记录
   */
  AlertRecord update(Long id, AlertRecord alertRecord);

  /**
   * 标记告警为已解决
   */
  AlertRecord resolve(Long id);

  /**
   * 批量标记告警为已解决
   */
  void resolveBatch(List<Long> ids);

  /**
   * 删除告警记录
   */
  void delete(List<Long> ids);
}
