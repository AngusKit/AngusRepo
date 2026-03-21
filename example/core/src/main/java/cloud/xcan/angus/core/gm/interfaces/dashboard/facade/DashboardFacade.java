package cloud.xcan.angus.core.gm.interfaces.dashboard.facade;

import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo.DashboardStatisticsVo;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo.RecentActivitiesVo;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo.SystemResourcesVo;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo.UserGrowthVo;

/**
 * 概览门面接口
 */
public interface DashboardFacade {

  /**
   * 获取概览统计数据
   */
  DashboardStatisticsVo getStatistics();

  /**
   * 获取用户增长趋势
   *
   * @param timeRange 时间范围：7DAYS, 30DAYS, 90DAYS, 6MONTHS, 1YEAR, ALL
   */
  UserGrowthVo getUserGrowth(String timeRange);

  /**
   * 获取系统资源使用情况
   */
  SystemResourcesVo getSystemResources();

  /**
   * 获取最近活动列表
   *
   * @param limit        返回记录数量
   * @param resourceType 资源类型筛选（可选）
   */
  RecentActivitiesVo getRecentActivities(Integer limit, ResourceType resourceType);
}
