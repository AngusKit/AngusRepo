package cloud.xcan.angus.core.gm.interfaces.interfaces.facade;

import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceRequestLogCreateDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceRequestLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceRequestLogStatisticsDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceRequestLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceRequestLogListVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.InterfaceRequestLogStatisticsVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

/**
 * API请求日志门面服务接口
 */
public interface InterfaceRequestLogFacade {

  /**
   * 批量创建日志记录
   */
  void batchCreate(List<InterfaceRequestLogCreateDto> dto);

  /**
   * 获取日志详情
   */
  InterfaceRequestLogDetailVo getDetail(Long id);

  /**
   * 获取日志列表（分页）
   */
  PageResult<InterfaceRequestLogListVo> list(InterfaceRequestLogFindDto dto);

  /**
   * 获取统计数据
   */
  InterfaceRequestLogStatisticsVo getStatistics(InterfaceRequestLogStatisticsDto dto);
}
