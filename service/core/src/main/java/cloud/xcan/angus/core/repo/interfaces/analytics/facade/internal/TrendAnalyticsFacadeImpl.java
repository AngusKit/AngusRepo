package cloud.xcan.angus.core.repo.interfaces.analytics.facade.internal;

import cloud.xcan.angus.core.repo.application.query.analytics.TrendAnalyticsQuery;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.TrendAnalyticsFacade;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.TrendQueryDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.FormatDistributionVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.TrendDataPointVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.TrendingArtifactVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.TrendingRepositoryVo;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TrendAnalyticsFacadeImpl implements TrendAnalyticsFacade {

  @Resource
  private TrendAnalyticsQuery trendAnalyticsQuery;

  @Override
  public List<TrendingArtifactVo> getTrendingArtifacts() {
    return trendAnalyticsQuery.getTrendingArtifacts(10);
  }

  @Override
  public List<TrendingRepositoryVo> getTrendingRepositories() {
    return trendAnalyticsQuery.getTrendingRepositories(10);
  }

  @Override
  public List<TrendDataPointVo> getDownloadTrend(TrendQueryDto dto) {
    return trendAnalyticsQuery.getDownloadTrend(dto.getPeriod(), dto.getStartDate(), dto.getEndDate());
  }

  @Override
  public List<TrendDataPointVo> getUploadTrend(TrendQueryDto dto) {
    return trendAnalyticsQuery.getUploadTrend(dto.getPeriod(), dto.getStartDate(), dto.getEndDate());
  }

  @Override
  public List<TrendDataPointVo> getStorageTrend(TrendQueryDto dto) {
    return trendAnalyticsQuery.getStorageTrend(dto.getPeriod(), dto.getStartDate(), dto.getEndDate());
  }

  @Override
  public List<TrendDataPointVo> getUserActivityTrend(TrendQueryDto dto) {
    return trendAnalyticsQuery.getUserActivityTrend(dto.getPeriod(), dto.getStartDate(), dto.getEndDate());
  }

  @Override
  public List<FormatDistributionVo> getFormatDistribution() {
    return trendAnalyticsQuery.getFormatDistribution();
  }
}
