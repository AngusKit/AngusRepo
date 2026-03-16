package cloud.xcan.angus.core.repo.application.query.activitylog.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.application.query.activitylog.ActivityLogQuery;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityAction;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityCategory;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLog;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLogListRepo;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLogRepo;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLogSearchRepo;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo.ActivityLogStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo.ActivityTrendVo;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo.ActivityUserListVo;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * 活动日志查询实现
 */
@Biz
@Transactional(readOnly = true)
public class ActivityLogQueryImpl implements ActivityLogQuery {

  @Resource
  private ActivityLogRepo activityLogRepo;

  @Resource
  private ActivityLogListRepo activityLogListRepo;

  @Resource
  private ActivityLogSearchRepo activityLogSearchRepo;

  @Override
  public Page<ActivityLog> find(GenericSpecification<ActivityLog> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<ActivityLog>>() {
      @Override
      protected Page<ActivityLog> process() {
        return fullTextSearch
            ? activityLogSearchRepo.find(spec.getCriteria(), pageable, ActivityLog.class, match)
            : activityLogListRepo.find(spec.getCriteria(), pageable, ActivityLog.class, null);
      }
    }.execute();
  }

  @Override
  public Optional<ActivityLog> findById(String id) {
    String tenantId = PrincipalContext.get().getTenantId().toString();
    return activityLogRepo.findByTenantIdAndId(tenantId, id);
  }

  @Override
  public ActivityLogStatisticsVo getStatistics(LocalDateTime startDate, LocalDateTime endDate) {
    String tenantId = PrincipalContext.get().getTenantId().toString();
    List<ActivityLog> allLogs = activityLogRepo.findAll();

    // 过滤租户
    allLogs = allLogs.stream()
        .filter(log -> tenantId.equals(log.getTenantId() != null ? log.getTenantId().toString() : null))
        .collect(Collectors.toList());

    // 按时间范围过滤
    if (startDate != null) {
      allLogs = allLogs.stream()
          .filter(log -> log.getTimestamp() != null && !log.getTimestamp().isBefore(startDate))
          .collect(Collectors.toList());
    }
    if (endDate != null) {
      allLogs = allLogs.stream()
          .filter(log -> log.getTimestamp() != null && !log.getTimestamp().isAfter(endDate))
          .collect(Collectors.toList());
    }

    LocalDate today = LocalDate.now();

    ActivityLogStatisticsVo statistics = new ActivityLogStatisticsVo();

    // 总日志数
    statistics.setTotalLogs((long) allLogs.size());

    // 今日日志数（简化实现）
    statistics.setLogsToday(0L);

    // 本周日志数（简化实现）
    statistics.setLogsThisWeek(0L);

    // 本月日志数（简化实现）
    statistics.setLogsThisMonth(0L);

    // 操作类型分布
    Map<ActivityAction, Long> actionDistribution = allLogs.stream()
        .collect(Collectors.groupingBy(
            log -> log.getAction() != null ? log.getAction() : ActivityAction.CREATE,
            Collectors.counting()));
    statistics.setActionDistribution(actionDistribution);

    // 分类分布
    Map<ActivityCategory, Long> categoryDistribution = allLogs.stream()
        .collect(Collectors.groupingBy(
            log -> log.getCategory() != null ? log.getCategory() : ActivityCategory.SYSTEM,
            Collectors.counting()));
    statistics.setCategoryDistribution(categoryDistribution);

    // Top 10活跃用户
    Map<String, Long> topUsers = allLogs.stream()
        .collect(Collectors.groupingBy(
            log -> log.getUser() != null ? log.getUser() : "Unknown",
            Collectors.counting()))
        .entrySet().stream()
        .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
        .limit(10)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    statistics.setTopUsers(topUsers);

    // Top 10活跃仓库
    Map<String, Long> topRepositories = allLogs.stream()
        .collect(Collectors.groupingBy(
            log -> log.getRepository() != null ? log.getRepository() : "Unknown",
            Collectors.counting()))
        .entrySet().stream()
        .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
        .limit(10)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    statistics.setTopRepositories(topRepositories);

    // 活动趋势（最近30天）（简化实现）
    List<ActivityTrendVo> activityTrend = new ArrayList<>();
    for (int i = 29; i >= 0; i--) {
      LocalDate date = today.minusDays(i);
      ActivityTrendVo trendVo = new ActivityTrendVo();
      trendVo.setDate(date.toString());
      trendVo.setCount(0L);
      trendVo.setActionBreakdown(Map.of());
      activityTrend.add(trendVo);
    }
    statistics.setActivityTrend(activityTrend);

    return statistics;
  }

  @Override
  public ActivityUserListVo getUniqueUsers() {
    String tenantId = PrincipalContext.get().getTenantId().toString();
    List<ActivityLog> allLogs = activityLogRepo.findAll();

    // 过滤租户并获取唯一用户
    List<String> users = allLogs.stream()
        .filter(log -> tenantId.equals(log.getTenantId() != null ? log.getTenantId().toString() : null))
        .map(ActivityLog::getUser)
        .filter(user -> user != null && !user.isEmpty())
        .distinct()
        .sorted()
        .collect(Collectors.toList());

    ActivityUserListVo vo = new ActivityUserListVo();
    vo.setUsers(users);
    return vo;
  }

  @Override
  public List<ActivityLog> findForExport(GenericSpecification<ActivityLog> spec) {
    // 导出时获取所有匹配的记录（不分页）
    return new BizTemplate<List<ActivityLog>>() {
      @Override
      protected List<ActivityLog> process() {
        // 使用ListRepo查询所有记录
        Page<ActivityLog> page = activityLogListRepo.find(spec.getCriteria(),
            PageRequest.of(0, 10000), ActivityLog.class, null);
        return page.getContent();
      }
    }.execute();
  }
}
