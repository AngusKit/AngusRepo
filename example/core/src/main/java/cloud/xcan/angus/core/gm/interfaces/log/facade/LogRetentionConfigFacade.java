package cloud.xcan.angus.core.gm.interfaces.log.facade;

import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.LogRetentionConfigCleanupDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.LogRetentionConfigFindDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.LogRetentionConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.LogRetentionConfigCleanupVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.LogRetentionConfigDetailVo;
import java.util.List;

/**
 * 日志清理配置门面接口
 */
public interface LogRetentionConfigFacade {

  /**
   * 更新配置
   */
  LogRetentionConfigDetailVo update(Long applicationId, LogRetentionConfigUpdateDto dto);

  /**
   * 批量更新配置
   */
  List<LogRetentionConfigDetailVo> batchUpdate(List<LogRetentionConfigUpdateDto> dto);

  /**
   * 查询列表
   */
  List<LogRetentionConfigDetailVo> findList(LogRetentionConfigFindDto dto);

  /**
   * 执行清理
   */
  LogRetentionConfigCleanupVo cleanup(Long applicationId, LogRetentionConfigCleanupDto dto);
}
