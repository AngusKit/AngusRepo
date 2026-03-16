package cloud.xcan.angus.core.repo.application.query.analytics.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.repo.application.query.analytics.AnalyticsQuery;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.DownloadAnalyticsVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.FormatUsageVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.RepositoryComparisonVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.UserActivityAnalyticsVo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Biz
@Transactional(readOnly = true)
public class AnalyticsQueryImpl implements AnalyticsQuery {

  @Override
  public DownloadAnalyticsVo getDownloadAnalytics(Integer period, LocalDate startDate, LocalDate endDate, Long repositoryId, String format) {
    return new BizTemplate<DownloadAnalyticsVo>() {
      @Override
      protected DownloadAnalyticsVo process() {
        DownloadAnalyticsVo vo = new DownloadAnalyticsVo();
        vo.setTotalDownloads(0L);
        vo.setAverageDailyDownloads(0L);
        vo.setPeakDownloads(0L);
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
        UserActivityAnalyticsVo vo = new UserActivityAnalyticsVo();
        vo.setActiveUsers(0L);
        vo.setTotalActions(0L);
        vo.setAverageDailyActiveUsers(0L);
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
        // TODO: Implement with actual data aggregation
        return new ArrayList<>();
      }
    }.execute();
  }

  @Override
  public List<FormatUsageVo> getFormatUsage(Integer period) {
    return new BizTemplate<List<FormatUsageVo>>() {
      @Override
      protected List<FormatUsageVo> process() {
        // TODO: Implement with actual data aggregation
        return new ArrayList<>();
      }
    }.execute();
  }
}
