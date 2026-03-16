package cloud.xcan.angus.core.repo.application.query.analytics.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.repo.application.query.analytics.AnalyticsQuery;
import cloud.xcan.angus.core.repo.domain.artifact.Artifact;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactRepo;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntityRepo;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.domain.user.UserProfileRepo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.DownloadAnalyticsVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.FormatUsageVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.RepositoryComparisonVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.UserActivityAnalyticsVo;
import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Biz
@Transactional(readOnly = true)
public class AnalyticsQueryImpl implements AnalyticsQuery {

  @Resource
  private ArtifactRepo artifactRepo;

  @Resource
  private RepoEntityRepo repoEntityRepo;

  @Resource
  private UserProfileRepo userProfileRepo;

  @Override
  public DownloadAnalyticsVo getDownloadAnalytics(Integer period, LocalDate startDate, LocalDate endDate, Long repositoryId, String format) {
    return new BizTemplate<DownloadAnalyticsVo>() {
      @Override
      protected DownloadAnalyticsVo process() {
        List<Artifact> artifacts;
        if (repositoryId != null) {
          artifacts = artifactRepo.findByRepositoryId(repositoryId);
        } else {
          artifacts = artifactRepo.findAll();
        }

        long totalDownloads = artifacts.stream()
            .mapToLong(a -> a.getDownloads() != null ? a.getDownloads() : 0L)
            .sum();
        long peakDownloads = artifacts.stream()
            .mapToLong(a -> a.getDownloads() != null ? a.getDownloads() : 0L)
            .max().orElse(0L);

        int days = period != null ? period : 30;
        long avgDaily = days > 0 ? totalDownloads / days : 0L;

        DownloadAnalyticsVo vo = new DownloadAnalyticsVo();
        vo.setTotalDownloads(totalDownloads);
        vo.setAverageDailyDownloads(avgDaily);
        vo.setPeakDownloads(peakDownloads);
        vo.setTrendData(new ArrayList<>());
        return vo;
      }
    }.execute();
  }

  @Override
  public UserActivityAnalyticsVo getUserActivityAnalytics(Integer period, LocalDate startDate, LocalDate endDate) {
    return new BizTemplate<UserActivityAnalyticsVo>() {
      @Override
      protected UserActivityAnalyticsVo process() {
        long totalUsers = userProfileRepo.count();

        UserActivityAnalyticsVo vo = new UserActivityAnalyticsVo();
        vo.setActiveUsers(totalUsers);
        vo.setTotalActions(0L);
        vo.setAverageDailyActiveUsers(totalUsers);
        vo.setTrendData(new ArrayList<>());
        return vo;
      }
    }.execute();
  }

  @Override
  public List<RepositoryComparisonVo> getRepositoryComparison(List<Long> repositoryIds, Integer period) {
    return new BizTemplate<List<RepositoryComparisonVo>>() {
      @Override
      protected List<RepositoryComparisonVo> process() {
        if (repositoryIds == null || repositoryIds.isEmpty()) {
          return new ArrayList<>();
        }

        List<RepositoryComparisonVo> result = new ArrayList<>();
        for (Long repoId : repositoryIds) {
          Optional<RepoEntity> repoOpt = repoEntityRepo.findById(repoId);
          if (repoOpt.isEmpty()) {
            continue;
          }
          RepoEntity repo = repoOpt.get();
          List<Artifact> repoArtifacts = artifactRepo.findByRepositoryId(repoId);

          long downloads = repoArtifacts.stream()
              .mapToLong(a -> a.getDownloads() != null ? a.getDownloads() : 0L)
              .sum();
          long storage = repoArtifacts.stream()
              .mapToLong(a -> a.getSizeBytes() != null ? a.getSizeBytes() : 0L)
              .sum();

          RepositoryComparisonVo vo = new RepositoryComparisonVo();
          vo.setRepositoryId(repo.getId());
          vo.setRepositoryName(repo.getName());
          vo.setFormat(repo.getFormat() != null ? repo.getFormat().getValue() : null);
          vo.setArtifactCount((long) repoArtifacts.size());
          vo.setDownloadCount(downloads);
          vo.setStorageBytes(storage);
          // Activity score based on downloads and artifact count
          vo.setActivityScore(downloads * 0.7 + repoArtifacts.size() * 0.3);
          result.add(vo);
        }
        return result;
      }
    }.execute();
  }

  @Override
  public List<FormatUsageVo> getFormatUsage(Integer period) {
    return new BizTemplate<List<FormatUsageVo>>() {
      @Override
      protected List<FormatUsageVo> process() {
        List<RepoEntity> allRepos = repoEntityRepo.findAll();
        Map<RepositoryFormat, List<RepoEntity>> byFormat = allRepos.stream()
            .filter(r -> r.getFormat() != null)
            .collect(Collectors.groupingBy(RepoEntity::getFormat));

        // Batch-load all artifacts and group by repository to avoid N+1 queries
        List<Artifact> allArtifacts = artifactRepo.findAll();
        Map<Long, List<Artifact>> artifactsByRepo = allArtifacts.stream()
            .collect(Collectors.groupingBy(Artifact::getRepositoryId));

        List<FormatUsageVo> result = new ArrayList<>();
        for (Map.Entry<RepositoryFormat, List<RepoEntity>> entry : byFormat.entrySet()) {
          FormatUsageVo vo = new FormatUsageVo();
          vo.setFormat(entry.getKey().getValue());
          vo.setRepositoryCount((long) entry.getValue().size());

          long artifactCount = entry.getValue().stream()
              .mapToLong(r -> r.getArtifacts() != null ? r.getArtifacts().longValue() : 0L)
              .sum();
          vo.setArtifactCount(artifactCount);

          long storage = entry.getValue().stream()
              .mapToLong(r -> r.getSizeBytes() != null ? r.getSizeBytes() : 0L)
              .sum();
          vo.setStorageBytes(storage);

          // Aggregate downloads from pre-loaded artifacts
          long downloads = 0L;
          for (RepoEntity repo : entry.getValue()) {
            List<Artifact> repoArtifacts = artifactsByRepo.getOrDefault(repo.getId(), List.of());
            downloads += repoArtifacts.stream()
                .mapToLong(a -> a.getDownloads() != null ? a.getDownloads() : 0L)
                .sum();
          }
          vo.setDownloadCount(downloads);
          vo.setGrowthRate(0.0);
          result.add(vo);
        }
        return result;
      }
    }.execute();
  }
}
