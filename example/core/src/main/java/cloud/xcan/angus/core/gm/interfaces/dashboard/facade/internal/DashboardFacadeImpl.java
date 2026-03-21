package cloud.xcan.angus.core.gm.interfaces.dashboard.facade.internal;

import cloud.xcan.angus.core.gm.application.query.dashboard.DashboardQuery;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.DashboardFacade;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo.DashboardStatisticsVo;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo.RecentActivitiesVo;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo.SystemResourcesVo;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo.UserGrowthVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 概览门面实现
 */
@Component
public class DashboardFacadeImpl implements DashboardFacade {

  @Resource
  private DashboardQuery dashboardQuery;

  @Override
  public DashboardStatisticsVo getStatistics() {
    return dashboardQuery.getStatistics();
  }

  @Override
  public UserGrowthVo getUserGrowth(String timeRange) {
    return dashboardQuery.getUserGrowth(timeRange);
  }

  @Override
  public SystemResourcesVo getSystemResources() {
    return dashboardQuery.getSystemResources();
  }

  @Override
  public RecentActivitiesVo getRecentActivities(Integer limit, ResourceType resourceType) {
    return dashboardQuery.getRecentActivities(limit, resourceType);
  }
}
