package cloud.xcan.angus.core.repo.application.query.analytics;

import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.FormatDistributionVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.TrendDataPointVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.TrendingArtifactVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.TrendingRepositoryVo;
import java.time.LocalDate;
import java.util.List;

public interface TrendAnalyticsQuery {
  List<TrendingArtifactVo> getTrendingArtifacts(Integer limit);
  List<TrendingRepositoryVo> getTrendingRepositories(Integer limit);
  List<TrendDataPointVo> getDownloadTrend(Integer period, LocalDate startDate, LocalDate endDate);
  List<TrendDataPointVo> getUploadTrend(Integer period, LocalDate startDate, LocalDate endDate);
  List<TrendDataPointVo> getStorageTrend(Integer period, LocalDate startDate, LocalDate endDate);
  List<TrendDataPointVo> getUserActivityTrend(Integer period, LocalDate startDate, LocalDate endDate);
  List<FormatDistributionVo> getFormatDistribution();
}
