package cloud.xcan.angus.core.repo.interfaces.dashboard.facade.internal;

import cloud.xcan.angus.core.repo.application.query.dashboard.DashboardQuery;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.DashboardFacade;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.DashboardOverviewVo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.RecentActivityVo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.StorageDistributionVo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.SystemMetricsVo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.TopRepositoryVo;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DashboardFacadeImpl implements DashboardFacade {

  @Resource
  private DashboardQuery dashboardQuery;

  @Override
  public DashboardOverviewVo getOverview() {
    return dashboardQuery.getOverview();
  }

  @Override
  public List<TopRepositoryVo> getTopRepositories() {
    return dashboardQuery.getTopRepositories();
  }

  @Override
  public List<RecentActivityVo> getRecentActivity() {
    return dashboardQuery.getRecentActivity();
  }

  @Override
  public List<StorageDistributionVo> getStorageDistribution() {
    return dashboardQuery.getStorageDistribution();
  }

  @Override
  public SystemMetricsVo getSystemMetrics() {
    return dashboardQuery.getSystemMetrics();
  }
}
