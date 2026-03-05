package cloud.xcan.angus.core.repo.application.query.analytics;

import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.DownloadAnalyticsVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.FormatUsageVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.RepositoryComparisonVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.UserActivityAnalyticsVo;
import java.time.LocalDate;
import java.util.List;

public interface AnalyticsQuery {
  DownloadAnalyticsVo getDownloadAnalytics(Integer period, LocalDate startDate, LocalDate endDate, Long repositoryId, String format);
  UserActivityAnalyticsVo getUserActivityAnalytics(Integer period, LocalDate startDate, LocalDate endDate);
  List<RepositoryComparisonVo> getRepositoryComparison(List<Long> repositoryIds, Integer period);
  List<FormatUsageVo> getFormatUsage(Integer period);
}
