package cloud.xcan.angus.core.repo.application.query.dashboard.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.repo.application.query.dashboard.DashboardQuery;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactRepo;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntityRepo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.DashboardOverviewVo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.RecentActivityVo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.StorageDistributionVo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.SystemMetricsVo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.TopRepositoryVo;
import jakarta.annotation.Resource;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Biz
@Transactional(readOnly = true)
public class DashboardQueryImpl implements DashboardQuery {

  @Resource
  private RepoEntityRepo repoEntityRepo;

  @Resource
  private ArtifactRepo artifactRepo;

  @Override
  public DashboardOverviewVo getOverview() {
    return new BizTemplate<DashboardOverviewVo>() {
      @Override
      protected DashboardOverviewVo process() {
        DashboardOverviewVo vo = new DashboardOverviewVo();
        vo.setTotalRepositories(repoEntityRepo.count());
        vo.setTotalArtifacts(artifactRepo.count());
        vo.setTotalDownloads(0L);
        vo.setTotalStorageBytes(0L);
        vo.setTotalUsers(0L);
        vo.setActiveUsers(0L);
        return vo;
      }
    }.execute();
  }

  @Override
  public List<TopRepositoryVo> getTopRepositories() {
    return new BizTemplate<List<TopRepositoryVo>>() {
      @Override
      protected List<TopRepositoryVo> process() {
        return new ArrayList<>();
      }
    }.execute();
  }

  @Override
  public List<RecentActivityVo> getRecentActivity() {
    return new BizTemplate<List<RecentActivityVo>>() {
      @Override
      protected List<RecentActivityVo> process() {
        return new ArrayList<>();
      }
    }.execute();
  }

  @Override
  public List<StorageDistributionVo> getStorageDistribution() {
    return new BizTemplate<List<StorageDistributionVo>>() {
      @Override
      protected List<StorageDistributionVo> process() {
        return new ArrayList<>();
      }
    }.execute();
  }

  @Override
  public SystemMetricsVo getSystemMetrics() {
    return new BizTemplate<SystemMetricsVo>() {
      @Override
      protected SystemMetricsVo process() {
        SystemMetricsVo vo = new SystemMetricsVo();
        Runtime runtime = Runtime.getRuntime();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

        vo.setJvmMaxMemory(runtime.maxMemory());
        vo.setJvmUsedMemory(runtime.totalMemory() - runtime.freeMemory());
        vo.setJvmFreeMemory(runtime.freeMemory());
        vo.setAvailableProcessors(runtime.availableProcessors());
        vo.setUptimeSeconds(runtimeBean.getUptime() / 1000);

        File root = new File("/");
        vo.setDiskTotalSpace(root.getTotalSpace());
        vo.setDiskFreeSpace(root.getFreeSpace());
        vo.setCpuUsage(0.0);
        return vo;
      }
    }.execute();
  }
}
