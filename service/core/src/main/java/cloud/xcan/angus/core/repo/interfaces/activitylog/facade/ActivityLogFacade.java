package cloud.xcan.angus.core.repo.interfaces.activitylog.facade;

import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto.ActivityLogBatchDeleteDto;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto.ActivityLogCreateDto;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto.ActivityLogExportDto;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto.ActivityLogFindDto;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo.ActivityLogStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo.ActivityLogVo;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo.ActivityUserListVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 活动日志Facade接口
 */
public interface ActivityLogFacade {

  /**
   * 创建活动日志
   */
  ActivityLogVo create(ActivityLogCreateDto dto);

  /**
   * 删除活动日志
   */
  void delete(String id);

  /**
   * 批量删除活动日志
   */
  void deleteBatch(ActivityLogBatchDeleteDto dto);

  /**
   * 查询活动日志详情
   */
  ActivityLogVo getById(String id);

  /**
   * 查询活动日志列表
   */
  PageResult<ActivityLogVo> list(ActivityLogFindDto dto);

  /**
   * 查询活动日志统计
   */
  ActivityLogStatisticsVo getStatistics(ActivityLogFindDto dto);

  /**
   * 导出活动日志
   */
  void export(ActivityLogExportDto dto, HttpServletResponse response) throws IOException;

  /**
   * 获取唯一用户列表
   */
  ActivityUserListVo getUniqueUsers();
}
