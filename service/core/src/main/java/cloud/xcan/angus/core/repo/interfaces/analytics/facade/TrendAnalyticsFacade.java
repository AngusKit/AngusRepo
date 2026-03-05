package cloud.xcan.angus.core.repo.interfaces.analytics.facade;

import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.TrendQueryDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.FormatDistributionVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.TrendDataPointVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.TrendingArtifactVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.TrendingRepositoryVo;
import java.util.List;

public interface TrendAnalyticsFacade {
  List<TrendingArtifactVo> getTrendingArtifacts();
  List<TrendingRepositoryVo> getTrendingRepositories();
  List<TrendDataPointVo> getDownloadTrend(TrendQueryDto dto);
  List<TrendDataPointVo> getUploadTrend(TrendQueryDto dto);
  List<TrendDataPointVo> getStorageTrend(TrendQueryDto dto);
  List<TrendDataPointVo> getUserActivityTrend(TrendQueryDto dto);
  List<FormatDistributionVo> getFormatDistribution();
}
