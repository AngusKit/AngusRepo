package cloud.xcan.angus.core.repo.application.query.analytics.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.repo.application.query.analytics.TrendAnalyticsQuery;
import cloud.xcan.angus.core.repo.domain.artifact.Artifact;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactRepo;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntityRepo;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.FormatDistributionVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.TrendDataPointVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.TrendingArtifactVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.TrendingRepositoryVo;
import jakarta.annotation.Resource;
import java.time.LocalDate;
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
public class TrendAnalyticsQueryImpl implements TrendAnalyticsQuery {

  @Resource
  private ArtifactRepo artifactRepo;

  @Resource
  private RepoEntityRepo repoEntityRepo;

  @Override
  public List<TrendingArtifactVo> getTrendingArtifacts(Integer limit) {
    return new BizTemplate<List<TrendingArtifactVo>>() {
      @Override
      protected List<TrendingArtifactVo> process() {
        int size = limit != null ? limit : 10;
        Page<Artifact> page = artifactRepo.findAll(
            PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "downloads")));
        List<TrendingArtifactVo> result = new ArrayList<>();
        for (Artifact artifact : page.getContent()) {
          result.add(new TrendingArtifactVo()
              .setId(artifact.getId())
              .setName(artifact.getName())
              .setRepositoryName(artifact.getRepositoryName())
              .setVersion(artifact.getVersion())
              .setDownloadCount(artifact.getDownloads() != null
                  ? artifact.getDownloads().longValue() : 0L)
              .setStars(artifact.getStars() != null ? artifact.getStars() : 0)
              .setGrowthRate(0.0));
        }
        return result;
      }
    }.execute();
  }

  @Override
  public List<TrendingRepositoryVo> getTrendingRepositories(Integer limit) {
    return new BizTemplate<List<TrendingRepositoryVo>>() {
      @Override
      protected List<TrendingRepositoryVo> process() {
        int size = limit != null ? limit : 10;
        Page<RepoEntity> page = repoEntityRepo.findAll(
            PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "artifacts")));
        List<TrendingRepositoryVo> result = new ArrayList<>();
        for (RepoEntity repo : page.getContent()) {
          long downloadCount = 0;
          List<Artifact> artifacts = artifactRepo.findByRepositoryId(repo.getId());
          for (Artifact artifact : artifacts) {
            downloadCount += artifact.getDownloads() != null ? artifact.getDownloads() : 0;
          }
          result.add(new TrendingRepositoryVo()
              .setId(repo.getId())
              .setName(repo.getName())
              .setFormat(repo.getFormat() != null ? repo.getFormat().getValue() : null)
              .setArtifactCount(repo.getArtifacts() != null
                  ? repo.getArtifacts().longValue() : 0L)
              .setDownloadCount(downloadCount)
              .setGrowthRate(0.0));
        }
        return result;
      }
    }.execute();
  }

  @Override
  public List<TrendDataPointVo> getDownloadTrend(Integer period, LocalDate startDate,
      LocalDate endDate) {
    return new BizTemplate<List<TrendDataPointVo>>() {
      @Override
      protected List<TrendDataPointVo> process() {
        return buildTrendDataPoints(startDate, endDate);
      }
    }.execute();
  }

  @Override
  public List<TrendDataPointVo> getUploadTrend(Integer period, LocalDate startDate,
      LocalDate endDate) {
    return new BizTemplate<List<TrendDataPointVo>>() {
      @Override
      protected List<TrendDataPointVo> process() {
        return buildTrendDataPoints(startDate, endDate);
      }
    }.execute();
  }

  @Override
  public List<TrendDataPointVo> getStorageTrend(Integer period, LocalDate startDate,
      LocalDate endDate) {
    return new BizTemplate<List<TrendDataPointVo>>() {
      @Override
      protected List<TrendDataPointVo> process() {
        return buildTrendDataPoints(startDate, endDate);
      }
    }.execute();
  }

  @Override
  public List<TrendDataPointVo> getUserActivityTrend(Integer period, LocalDate startDate,
      LocalDate endDate) {
    return new BizTemplate<List<TrendDataPointVo>>() {
      @Override
      protected List<TrendDataPointVo> process() {
        return buildTrendDataPoints(startDate, endDate);
      }
    }.execute();
  }

  @Override
  public List<FormatDistributionVo> getFormatDistribution() {
    return new BizTemplate<List<FormatDistributionVo>>() {
      @Override
      protected List<FormatDistributionVo> process() {
        List<RepoEntity> allRepos = repoEntityRepo.findAll();
        long totalRepos = allRepos.size();

        // Group repos by format to avoid repeated DB calls
        Map<RepositoryFormat, List<RepoEntity>> reposByFormat = allRepos.stream()
            .filter(r -> r.getFormat() != null)
            .collect(Collectors.groupingBy(RepoEntity::getFormat));

        List<FormatDistributionVo> result = new ArrayList<>();
        for (RepositoryFormat format : RepositoryFormat.values()) {
          List<RepoEntity> repos = reposByFormat.getOrDefault(format, List.of());
          if (repos.isEmpty()) {
            continue;
          }
          long artifactCount = 0;
          long storageBytes = 0;
          for (RepoEntity repo : repos) {
            List<Artifact> artifacts = artifactRepo.findByRepositoryId(repo.getId());
            artifactCount += artifacts.size();
            for (Artifact artifact : artifacts) {
              storageBytes += artifact.getSizeBytes() != null ? artifact.getSizeBytes() : 0;
            }
          }
          double percentage = totalRepos > 0 ? (repos.size() * 100.0) / totalRepos : 0.0;
          result.add(new FormatDistributionVo()
              .setFormat(format.getValue())
              .setRepositoryCount((long) repos.size())
              .setArtifactCount(artifactCount)
              .setStorageBytes(storageBytes)
              .setPercentage(Math.round(percentage * 100.0) / 100.0));
        }
        return result;
      }
    }.execute();
  }

  /**
   * Builds baseline trend data points for the given date range. Returns one data point per day
   * with value 0, to be enriched once proper time-series aggregation queries are available.
   */
  private List<TrendDataPointVo> buildTrendDataPoints(LocalDate startDate, LocalDate endDate) {
    LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
    LocalDate end = endDate != null ? endDate : LocalDate.now();
    List<TrendDataPointVo> points = new ArrayList<>();
    for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
      points.add(new TrendDataPointVo().setDate(date).setValue(0L));
    }
    return points;
  }
}
