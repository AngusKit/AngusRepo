package cloud.xcan.angus.core.repo.interfaces.dashboard.facade;

import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.DashboardOverviewVo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.RecentActivityVo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.StorageDistributionVo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.SystemMetricsVo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.TopRepositoryVo;
import java.util.List;

public interface DashboardFacade {
  DashboardOverviewVo getOverview();
  List<TopRepositoryVo> getTopRepositories();
  List<RecentActivityVo> getRecentActivity();
  List<StorageDistributionVo> getStorageDistribution();
  SystemMetricsVo getSystemMetrics();
}
