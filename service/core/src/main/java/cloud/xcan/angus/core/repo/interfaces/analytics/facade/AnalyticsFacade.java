package cloud.xcan.angus.core.repo.interfaces.analytics.facade;

import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.AnalyticsExportDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.DownloadAnalyticsDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.FormatUsageDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.RepositoryComparisonDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.UserActivityAnalyticsDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.DownloadAnalyticsVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.FormatUsageVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.RepositoryComparisonVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.UserActivityAnalyticsVo;
import java.util.List;

public interface AnalyticsFacade {
  DownloadAnalyticsVo getDownloadAnalytics(DownloadAnalyticsDto dto);
  UserActivityAnalyticsVo getUserActivityAnalytics(UserActivityAnalyticsDto dto);
  List<RepositoryComparisonVo> getRepositoryComparison(RepositoryComparisonDto dto);
  String exportReport(AnalyticsExportDto dto);
  List<FormatUsageVo> getFormatUsage(FormatUsageDto dto);
}
