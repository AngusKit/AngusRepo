package cloud.xcan.angus.core.repo.interfaces.analytics.facade.internal;

import cloud.xcan.angus.core.repo.application.query.analytics.AnalyticsQuery;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.AnalyticsFacade;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.AnalyticsExportDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.DownloadAnalyticsDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.FormatUsageDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.RepositoryComparisonDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.UserActivityAnalyticsDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.DownloadAnalyticsVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.FormatUsageVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.RepositoryComparisonVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.UserActivityAnalyticsVo;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsFacadeImpl implements AnalyticsFacade {

  @Resource
  private AnalyticsQuery analyticsQuery;

  @Override
  public DownloadAnalyticsVo getDownloadAnalytics(DownloadAnalyticsDto dto) {
    return analyticsQuery.getDownloadAnalytics(dto.getPeriod(), dto.getStartDate(), dto.getEndDate(), dto.getRepositoryId(), dto.getFormat());
  }

  @Override
  public UserActivityAnalyticsVo getUserActivityAnalytics(UserActivityAnalyticsDto dto) {
    return analyticsQuery.getUserActivityAnalytics(dto.getPeriod(), dto.getStartDate(), dto.getEndDate());
  }

  @Override
  public List<RepositoryComparisonVo> getRepositoryComparison(RepositoryComparisonDto dto) {
    return analyticsQuery.getRepositoryComparison(dto.getRepositoryIds(), dto.getPeriod());
  }

  @Override
  public String exportReport(AnalyticsExportDto dto) {
    // TODO: Implement async report generation
    return "Report generation started";
  }

  @Override
  public List<FormatUsageVo> getFormatUsage(FormatUsageDto dto) {
    return analyticsQuery.getFormatUsage(dto.getPeriod());
  }
}
