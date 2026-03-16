package cloud.xcan.angus.core.repo.application.query.dashboard.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.repo.application.query.dashboard.DashboardQuery;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLog;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLogRepo;
import cloud.xcan.angus.core.repo.domain.artifact.Artifact;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactRepo;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntityRepo;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.domain.user.UserProfileRepo;
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
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@Biz
@Transactional(readOnly = true)
public class DashboardQueryImpl implements DashboardQuery {

  @Resource
  private RepoEntityRepo repoEntityRepo;

  @Resource
  private ArtifactRepo artifactRepo;

  @Resource
  private UserProfileRepo userProfileRepo;

  @Resource
  private ActivityLogRepo activityLogRepo;

  @Override
  public DashboardOverviewVo getOverview() {
    return new BizTemplate<DashboardOverviewVo>() {
      @Override
      protected DashboardOverviewVo process() {
        DashboardOverviewVo vo = new DashboardOverviewVo();
        vo.setTotalRepositories(repoEntityRepo.count());
        vo.setTotalArtifacts(artifactRepo.count());

        // Aggregate total downloads from all artifacts
        List<Artifact> allArtifacts = artifactRepo.findAll();
        long totalDownloads = allArtifacts.stream()
            .mapToLong(a -> a.getDownloads() != null ? a.getDownloads() : 0L)
            .sum();
        vo.setTotalDownloads(totalDownloads);

        // Aggregate total storage from all artifacts
        long totalStorage = allArtifacts.stream()
            .mapToLong(a -> a.getSizeBytes() != null ? a.getSizeBytes() : 0L)
            .sum();
        vo.setTotalStorageBytes(totalStorage);

        // Get user counts
        long totalUsers = userProfileRepo.count();
        vo.setTotalUsers(totalUsers);
        vo.setActiveUsers(totalUsers);
        return vo;
      }
    }.execute();
  }

  @Override
  public List<TopRepositoryVo> getTopRepositories() {
    return new BizTemplate<List<TopRepositoryVo>>() {
      @Override
      protected List<TopRepositoryVo> process() {
        // Get top 10 repositories by artifact count
        Page<RepoEntity> page = repoEntityRepo.findAll(
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "artifacts")));
        List<RepoEntity> repos = page.getContent();
        if (repos.isEmpty()) {
          return new ArrayList<>();
        }

        // Batch-load all artifacts for these repos to avoid N+1 queries
        List<Artifact> allArtifacts = artifactRepo.findAll();
        Map<Long, List<Artifact>> artifactsByRepo = allArtifacts.stream()
            .collect(Collectors.groupingBy(Artifact::getRepositoryId));

        List<TopRepositoryVo> result = new ArrayList<>();
        for (RepoEntity repo : repos) {
          TopRepositoryVo vo = new TopRepositoryVo();
          vo.setId(repo.getId());
          vo.setName(repo.getName());
          vo.setFormat(repo.getFormat() != null ? repo.getFormat().getValue() : null);
          vo.setArtifactCount(repo.getArtifacts() != null ? repo.getArtifacts().longValue() : 0L);
          vo.setStorageBytes(repo.getSizeBytes() != null ? repo.getSizeBytes() : 0L);

          List<Artifact> repoArtifacts = artifactsByRepo.getOrDefault(repo.getId(), List.of());
          long downloads = repoArtifacts.stream()
              .mapToLong(a -> a.getDownloads() != null ? a.getDownloads() : 0L)
              .sum();
          vo.setDownloadCount(downloads);
          result.add(vo);
        }
        return result;
      }
    }.execute();
  }

  @Override
  public List<RecentActivityVo> getRecentActivity() {
    return new BizTemplate<List<RecentActivityVo>>() {
      @Override
      protected List<RecentActivityVo> process() {
        // Get recent activity logs, ordered by timestamp descending
        Page<ActivityLog> page = activityLogRepo.findAll(
            PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "timestamp")));
        List<RecentActivityVo> result = new ArrayList<>();
        for (ActivityLog log : page.getContent()) {
          RecentActivityVo vo = new RecentActivityVo();
          vo.setId(log.getId() != null ? (long) Math.abs(log.getId().hashCode()) : null);
          vo.setAction(log.getAction() != null ? log.getAction().name() : null);
          vo.setTargetType(log.getCategory() != null ? log.getCategory().name() : null);
          vo.setTargetName(log.getArtifact());
          vo.setUserName(log.getUser());
          vo.setDescription(log.getDetails());
          vo.setCreatedDate(log.getTimestamp());
          result.add(vo);
        }
        return result;
      }
    }.execute();
  }

  @Override
  public List<StorageDistributionVo> getStorageDistribution() {
    return new BizTemplate<List<StorageDistributionVo>>() {
      @Override
      protected List<StorageDistributionVo> process() {
        // Get all repos and group by format
        List<RepoEntity> allRepos = repoEntityRepo.findAll();
        Map<RepositoryFormat, List<RepoEntity>> byFormat = allRepos.stream()
            .filter(r -> r.getFormat() != null)
            .collect(Collectors.groupingBy(RepoEntity::getFormat));

        long totalStorage = allRepos.stream()
            .mapToLong(r -> r.getSizeBytes() != null ? r.getSizeBytes() : 0L)
            .sum();

        List<StorageDistributionVo> result = new ArrayList<>();
        for (Map.Entry<RepositoryFormat, List<RepoEntity>> entry : byFormat.entrySet()) {
          StorageDistributionVo vo = new StorageDistributionVo();
          vo.setFormat(entry.getKey().getValue());
          long formatStorage = entry.getValue().stream()
              .mapToLong(r -> r.getSizeBytes() != null ? r.getSizeBytes() : 0L)
              .sum();
          vo.setStorageBytes(formatStorage);
          long formatArtifacts = entry.getValue().stream()
              .mapToLong(r -> r.getArtifacts() != null ? r.getArtifacts().longValue() : 0L)
              .sum();
          vo.setArtifactCount(formatArtifacts);
          vo.setPercentage(totalStorage > 0 ? (double) formatStorage / totalStorage * 100.0 : 0.0);
          result.add(vo);
        }
        return result;
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
