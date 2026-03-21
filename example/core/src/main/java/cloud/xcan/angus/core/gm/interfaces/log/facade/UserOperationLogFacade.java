package cloud.xcan.angus.core.gm.interfaces.log.facade;

import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.UserOperationLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.UserOperationLogStatisticsDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.UserOperationLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.UserOperationLogStatisticsVo;
import cloud.xcan.angus.remote.PageResult;

/**
 * 用户操作日志门面服务接口
 */
public interface UserOperationLogFacade {

  /**
   * 获取日志详情
   */
  UserOperationLogDetailVo getDetail(Long id);

  /**
   * 获取日志列表（分页）
   */
  PageResult<UserOperationLogDetailVo> list(UserOperationLogFindDto dto);

  /**
   * 获取统计数据
   */
  UserOperationLogStatisticsVo getStatistics(UserOperationLogStatisticsDto dto);
}
