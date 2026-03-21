package cloud.xcan.angus.core.gm.interfaces.log.facade;

import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogBatchDeleteDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogContentDto;
import cloud.xcan.angus.core.gm.infra.utils.DownloadResult;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogStatisticsDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.SystemLogContentVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.SystemLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.SystemLogStatisticsVo;
import cloud.xcan.angus.remote.PageResult;
import java.io.IOException;

/**
 * 系统日志门面服务接口
 */
public interface SystemLogFacade {

  /**
   * 获取日志详情
   */
  SystemLogDetailVo getDetail(Long id);

  /**
   * 获取日志列表（分页）
   */
  PageResult<SystemLogDetailVo> list(SystemLogFindDto dto);

  /**
   * 获取日志文件内容
   */
  SystemLogContentVo getContent(Long id, SystemLogContentDto dto);

  /**
   * 下载日志文件，返回 Resource 及元数据
   */
  DownloadResult download(Long id) throws IOException;

  /**
   * 删除日志文件
   */
  void delete(Long id, Boolean permanent);

  /**
   * 批量删除日志文件
   */
  void batchDelete(SystemLogBatchDeleteDto dto);

  /**
   * 获取统计数据
   */
  SystemLogStatisticsVo getStatistics(SystemLogStatisticsDto dto);
}
