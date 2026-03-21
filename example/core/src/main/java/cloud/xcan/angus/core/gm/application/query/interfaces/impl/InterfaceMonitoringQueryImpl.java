package cloud.xcan.angus.core.gm.application.query.interfaces.impl;

import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.parseDate;
import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.parsePeriod;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.interfaces.InterfaceMonitoringQuery;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLog;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLogInfoRepo;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLogRepo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.ErrorRequestFindDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceStatsFindDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.ErrorRequestDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.ErrorRequestVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceMonitoringOverviewVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceStatsDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceStatsVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.RealtimeQpsVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.RealtimeResponseTimeVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.RealtimeStatusCodeDistributionVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.TopCallsVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.TopErrorsVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.TopSlowVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class InterfaceMonitoringQueryImpl implements InterfaceMonitoringQuery {

  @Resource
  private InterfaceRequestLogInfoRepo interfaceRequestLogInfoRepo;

  @Resource
  private InterfaceRequestLogRepo interfaceRequestLogRepo;

  @Override
  public InterfaceMonitoringOverviewVo getOverview() {
    return new BizTemplate<InterfaceMonitoringOverviewVo>() {
      @Override
      protected InterfaceMonitoringOverviewVo process() {
        InterfaceMonitoringOverviewVo vo = new InterfaceMonitoringOverviewVo();

        // 查询最近1小时的数据
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusHours(1);

        // 总请求数
        long totalRequests = interfaceRequestLogInfoRepo.countByDateRange(startDate, endDate, null);
        vo.setTotalRequests(totalRequests);

        // 成功请求数
        long successRequests = interfaceRequestLogInfoRepo.countSuccessByDateRange(startDate,
            endDate, null);
        vo.setSuccessRequests(successRequests);

        // 失败请求数
        long failedRequests = interfaceRequestLogInfoRepo.countErrorByDateRange(startDate, endDate,
            null);
        vo.setFailedRequests(failedRequests);

        // 平均响应时间
        Double avgResponseTime = interfaceRequestLogInfoRepo.avgResponseTimeByDateRange(startDate,
            endDate, null);
        vo.setAvgResponseTime(avgResponseTime != null ? avgResponseTime.intValue() : 0);

        // 当前QPS（最近1分钟）
        LocalDateTime qpsStartDate = endDate.minusMinutes(1);
        long recentRequests = interfaceRequestLogInfoRepo.countRecentRequests(qpsStartDate, null);
        vo.setQps((int) recentRequests);

        // 错误率
        if (totalRequests > 0) {
          vo.setErrorRate((double) failedRequests / totalRequests * 100);
        } else {
          vo.setErrorRate(0.0);
        }

        // 慢请求数量（超过1秒）
        long slowRequestCount = interfaceRequestLogInfoRepo.countSlowRequests(startDate, endDate,
            1000L, null);
        vo.setSlowRequestCount((int) slowRequestCount);

        return vo;
      }
    }.execute();
  }

  @Override
  public PageResult<InterfaceStatsVo> listStats(InterfaceStatsFindDto dto) {
    return new BizTemplate<PageResult<InterfaceStatsVo>>() {
      @Override
      protected PageResult<InterfaceStatsVo> process() {
        // 解析时间范围
        LocalDateTime startDate = parseDate(dto.getStartDate(), false);
        LocalDateTime endDate = parseDate(dto.getEndDate(), true);
        if (endDate == null) {
          endDate = LocalDateTime.now();
        }
        if (startDate == null) {
          startDate = endDate.minusHours(24);
        }

        // 查询统计数据
        List<Object[]> stats = interfaceRequestLogInfoRepo.statsByServiceAndUriAndMethod(
            startDate, endDate, dto.getServiceName(), dto.getUri(), dto.getMethod(), null);

        // 转换为VO列表
        List<InterfaceStatsVo> voList = new ArrayList<>();
        for (Object[] row : stats) {
          InterfaceStatsVo vo = new InterfaceStatsVo();
          vo.setServiceName((String) row[0]);
          vo.setUri((String) row[1]);
          vo.setMethod((String) row[2]);
          vo.setCalls(((Number) row[3]).longValue());
          vo.setAvgResponseTime(((Number) row[4]).intValue());
          long errorCount = ((Number) row[5]).longValue();
          vo.setErrorCount(errorCount);
          long calls = vo.getCalls();
          vo.setErrorRate(calls > 0 ? (double) errorCount / calls * 100 : 0.0);
          voList.add(vo);
        }

        // 排序
        String sortBy = dto.getSortBy();
        String order = dto.getOrder();
        if (StringUtils.hasText(sortBy)) {
          boolean ascending = !"desc".equalsIgnoreCase(order);
          switch (sortBy.toLowerCase()) {
            case "calls":
              voList.sort((a, b) -> ascending
                  ? Long.compare(a.getCalls(), b.getCalls())
                  : Long.compare(b.getCalls(), a.getCalls()));
              break;
            case "avgtime":
              voList.sort((a, b) -> ascending
                  ? Integer.compare(a.getAvgResponseTime(), b.getAvgResponseTime())
                  : Integer.compare(b.getAvgResponseTime(), a.getAvgResponseTime()));
              break;
            case "errorrate":
              voList.sort((a, b) -> ascending
                  ? Double.compare(a.getErrorRate(), b.getErrorRate())
                  : Double.compare(b.getErrorRate(), a.getErrorRate()));
              break;
          }
        }

        // 分页
        int page = dto.getPage() != null && dto.getPage() > 0 ? dto.getPage() - 1 : 0;
        int size = dto.getSize() != null && dto.getSize() > 0 ? dto.getSize() : 20;
        int total = voList.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, total);
        List<InterfaceStatsVo> pagedList =
            fromIndex < total ? voList.subList(fromIndex, toIndex) : new ArrayList<>();
        return PageResult.of(total, pagedList);
      }
    }.execute();
  }

  @Override
  public InterfaceStatsDetailVo getStatsDetail(String serviceName, String uri,
      LocalDateTime startDate, LocalDateTime endDate, String period) {
    return new BizTemplate<InterfaceStatsDetailVo>() {
      @Override
      protected InterfaceStatsDetailVo process() {
        InterfaceStatsDetailVo vo = new InterfaceStatsDetailVo();
        vo.setServiceName(serviceName);
        vo.setPath(uri);

        // 解析时间范围
        LocalDateTime finalStartDate;
        LocalDateTime finalEndDate;
        if (startDate == null || endDate == null) {
          LocalDateTime[] periodRange = parsePeriod(period);
          finalStartDate = periodRange[0];
          finalEndDate = periodRange[1];
        } else {
          finalStartDate = startDate;
          finalEndDate = endDate;
        }

        // 设置周期
        InterfaceStatsDetailVo.Period periodVo = new InterfaceStatsDetailVo.Period();
        periodVo.setStartDate(
            finalStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        periodVo.setEndDate(
            finalEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        vo.setPeriod(periodVo);

        // 查询汇总数据
        List<Object[]> stats = interfaceRequestLogInfoRepo.statsByServiceAndUriAndMethod(
            finalStartDate, finalEndDate, serviceName, uri, null, null);

        if (!stats.isEmpty()) {
          Object[] row = stats.get(0);
          String method = (String) row[2];
          vo.setMethod(method);

          InterfaceStatsDetailVo.Summary summary = new InterfaceStatsDetailVo.Summary();
          long totalCalls = ((Number) row[3]).longValue();
          summary.setTotalCalls(totalCalls);
          long errorCount = ((Number) row[5]).longValue();
          summary.setFailedCalls(errorCount);
          summary.setSuccessCalls(totalCalls - errorCount);
          summary.setAvgResponseTime(((Number) row[4]).intValue());
          summary.setErrorRate(totalCalls > 0 ? (double) errorCount / totalCalls * 100 : 0.0);
          vo.setSummary(summary);
        } else {
          // 如果没有数据，设置默认值
          InterfaceStatsDetailVo.Summary summary = new InterfaceStatsDetailVo.Summary();
          summary.setTotalCalls(0L);
          summary.setSuccessCalls(0L);
          summary.setFailedCalls(0L);
          summary.setAvgResponseTime(0);
          summary.setErrorRate(0.0);
          vo.setSummary(summary);
        }

        // 查询时间线数据
        List<Object[]> timelineData = interfaceRequestLogInfoRepo.statsDetailByTime(
            serviceName, uri, finalStartDate, finalEndDate, null);
        List<InterfaceStatsDetailVo.TimelineItem> timeline = new ArrayList<>();
        for (Object[] row : timelineData) {
          InterfaceStatsDetailVo.TimelineItem item = new InterfaceStatsDetailVo.TimelineItem();
          item.setTime((String) row[0]);
          item.setCalls(((Number) row[1]).longValue());
          item.setAvgResponseTime(((Number) row[2]).intValue());
          long errorCount = ((Number) row[3]).longValue();
          long calls = item.getCalls();
          item.setErrorRate(calls > 0 ? (double) errorCount / calls * 100 : 0.0);
          timeline.add(item);
        }
        vo.setTimeline(timeline);

        // 查询响应时间分布
        List<Object[]> responseTimeDist = interfaceRequestLogInfoRepo.statsResponseTimeDistribution(
            serviceName, uri, finalStartDate, finalEndDate, null);
        Map<String, Long> responseTimeDistribution = new LinkedHashMap<>();
        for (Object[] row : responseTimeDist) {
          responseTimeDistribution.put((String) row[0], ((Number) row[1]).longValue());
        }
        vo.setResponseTimeDistribution(responseTimeDistribution);

        // 查询状态码分布
        List<Object[]> statusCodeDist = interfaceRequestLogInfoRepo.statsStatusCodeDistribution(
            serviceName, uri, finalStartDate, finalEndDate, null);
        Map<String, Long> statusCodeDistribution = new LinkedHashMap<>();
        for (Object[] row : statusCodeDist) {
          statusCodeDistribution.put((String) row[0], ((Number) row[1]).longValue());
        }
        vo.setStatusCodeDistribution(statusCodeDistribution);

        return vo;
      }
    }.execute();
  }

  @Override
  public PageResult<ErrorRequestVo> listErrorRequests(ErrorRequestFindDto dto) {
    return new BizTemplate<PageResult<ErrorRequestVo>>() {
      @Override
      protected PageResult<ErrorRequestVo> process() {
        // 构建查询条件 - 使用 Specification 手动构建
        Specification<InterfaceRequestLog> spec = (root, query, cb) -> {
          List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

          // 只查询错误请求（status >= 400）
          predicates.add(cb.greaterThanOrEqualTo(root.get("status"), 400));

          // 服务名称筛选
          if (StringUtils.hasText(dto.getServiceName())) {
            predicates.add(cb.equal(root.get("serviceName"), dto.getServiceName()));
          }

          // 路径筛选（注意DTO中使用的是path字段）
          if (StringUtils.hasText(dto.getPath())) {
            predicates.add(cb.like(root.get("uri"), "%" + dto.getPath() + "%"));
          }

          // 状态码筛选
          if (dto.getStatusCode() != null) {
            predicates.add(cb.equal(root.get("status"), dto.getStatusCode()));
          }

          // 日期范围筛选
          final LocalDateTime startDate = parseDate(dto.getStartDate(), false);
          final LocalDateTime endDate = parseDate(dto.getEndDate(), true);
          if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("requestDate"), startDate));
          }
          if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("requestDate"), endDate));
          }

          return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        // 分页
        int page = dto.getPage() != null && dto.getPage() > 0 ? dto.getPage() - 1 : 0;
        int size = dto.getSize() != null && dto.getSize() > 0 ? dto.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestDate"));

        Page<InterfaceRequestLog> logPage = interfaceRequestLogRepo.findAll(spec, pageable);

        // 转换为VO
        List<ErrorRequestVo> voList = logPage.getContent().stream().map(log -> {
          ErrorRequestVo vo = new ErrorRequestVo();
          vo.setId(log.getId());
          vo.setTraceId(log.getRequestId());
          vo.setServiceName(log.getServiceName());
          vo.setUri(log.getUri());
          vo.setMethod(log.getMethod());
          vo.setRequestDate(log.getRequestDate());
          vo.setDuration(log.getElapsedMillis().intValue());
          vo.setStatusCode(log.getStatus());
          vo.setIpAddress(log.getRemote());
          vo.setUserId(log.getUserId() != null ? log.getUserId().toString() : null);
          return vo;
        }).collect(Collectors.toList());

        return PageResult.of(logPage.getTotalElements(), voList);
      }
    }.execute();
  }

  @Override
  public ErrorRequestDetailVo getErrorRequestDetail(Long id) {
    return new BizTemplate<ErrorRequestDetailVo>() {
      @Override
      protected ErrorRequestDetailVo process() {
        InterfaceRequestLog log = interfaceRequestLogRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("错误请求记录「{0}」不存在", new Object[]{id}));

        ErrorRequestDetailVo vo = new ErrorRequestDetailVo();
        vo.setId(log.getId());
        vo.setTraceId(log.getRequestId());
        vo.setServiceName(log.getServiceName());
        vo.setPath(log.getUri());
        vo.setMethod(log.getMethod());
        vo.setRequestTime(log.getRequestDate());
        vo.setDuration(log.getElapsedMillis().intValue());
        vo.setStatusCode(log.getStatus());
        vo.setIpAddress(log.getRemote());
        vo.setUserId(log.getUserId() != null ? log.getUserId().toString() : null);
        vo.setUserName(log.getUserName());

        // 转换请求头
        if (log.getRequestHeaders() != null) {
          Map<String, String> headers = new HashMap<>();
          log.getRequestHeaders().forEach((key, values) -> {
            headers.put(key, String.join(", ", values));
          });
          vo.setRequestHeaders(headers);
        }

        vo.setRequestBody(log.getRequestBody());
        vo.setResponseBody(log.getResponseBody());

        return vo;
      }
    }.execute();
  }

  @Override
  public RealtimeQpsVo getRealtimeQps(String period) {
    return new BizTemplate<RealtimeQpsVo>() {
      @Override
      protected RealtimeQpsVo process() {
        RealtimeQpsVo vo = new RealtimeQpsVo();
        LocalDateTime[] periodRange = parsePeriod(period);
        LocalDateTime startDate = periodRange[0];
        LocalDateTime endDate = periodRange[1];

        // 查询最近1分钟的数据作为当前QPS
        LocalDateTime recentStart = endDate.minusMinutes(1);
        long recentRequests = interfaceRequestLogInfoRepo.countRecentRequests(recentStart, null);
        vo.setCurrent((int) recentRequests);

        List<Object[]> timelineData = interfaceRequestLogInfoRepo.qpsTimeline(
            startDate, endDate, null);

        int peak = 0;
        long total = 0;
        List<RealtimeQpsVo.TimelineItem> timeline = new ArrayList<>();
        for (Object[] row : timelineData) {
          int qps = ((Number) row[1]).intValue();
          peak = Math.max(peak, qps);
          total += qps;

          RealtimeQpsVo.TimelineItem item = new RealtimeQpsVo.TimelineItem();
          item.setTime((String) row[0]);
          item.setQps(qps);
          timeline.add(item);
        }

        vo.setPeak(peak);
        vo.setAverage(timeline.isEmpty() ? 0 : (int) (total / timeline.size()));
        vo.setTimeline(timeline);

        return vo;
      }
    }.execute();
  }

  @Override
  public RealtimeResponseTimeVo getRealtimeResponseTime(String period) {
    return new BizTemplate<RealtimeResponseTimeVo>() {
      @Override
      protected RealtimeResponseTimeVo process() {
        RealtimeResponseTimeVo vo = new RealtimeResponseTimeVo();
        LocalDateTime[] periodRange = parsePeriod(period);
        LocalDateTime startDate = periodRange[0];
        LocalDateTime endDate = periodRange[1];

        // 当前值：最近1分钟的平均响应时间
        LocalDateTime recentStart = endDate.minusMinutes(1);
        Double avgResponseTime = interfaceRequestLogInfoRepo.avgResponseTimeByDateRange(
            recentStart, endDate, null);
        vo.setCurrent(avgResponseTime != null ? avgResponseTime.intValue() : 0);

        // 使用原始数据计算各时间桶的 avg、p50、p95、p99、max
        List<Object[]> rawData = interfaceRequestLogInfoRepo.findResponseTimeRawByDateRange(
            startDate, endDate, null);

        Map<String, List<Long>> byTime = new LinkedHashMap<>();
        for (Object[] row : rawData) {
          String time = (String) row[0];
          long elapsed = ((Number) row[1]).longValue();
          byTime.computeIfAbsent(time, k -> new ArrayList<>()).add(elapsed);
        }

        List<Long> allResponseTimes = new ArrayList<>();
        List<RealtimeResponseTimeVo.TimelineItem> timeline = new ArrayList<>();

        for (Map.Entry<String, List<Long>> e : byTime.entrySet()) {
          List<Long> vals = e.getValue();
          vals.sort(Long::compareTo);
          allResponseTimes.addAll(vals);

          RealtimeResponseTimeVo.TimelineItem item = new RealtimeResponseTimeVo.TimelineItem();
          item.setTime(e.getKey());
          item.setAvg((int) vals.stream().mapToLong(Long::longValue).average().orElse(0));
          item.setP50(percentile(vals, 0.5));
          item.setP95(percentile(vals, 0.95));
          item.setP99(percentile(vals, 0.99));
          item.setMax(vals.isEmpty() ? 0 : vals.get(vals.size() - 1).intValue());
          timeline.add(item);
        }

        vo.setTimeline(timeline);

        // 顶层汇总：整个周期内的 avg、p50、p95、p99、max
        if (!allResponseTimes.isEmpty()) {
          allResponseTimes.sort(Long::compareTo);
          vo.setAvg((int) allResponseTimes.stream().mapToLong(Long::longValue).average().orElse(0));
          vo.setP50(percentile(allResponseTimes, 0.5));
          vo.setP95(percentile(allResponseTimes, 0.95));
          vo.setP99(percentile(allResponseTimes, 0.99));
          vo.setMax(allResponseTimes.get(allResponseTimes.size() - 1).intValue());
        } else {
          vo.setAvg(0);
          vo.setP50(0);
          vo.setP95(0);
          vo.setP99(0);
          vo.setMax(0);
        }

        return vo;
      }
    }.execute();
  }

  private static int percentile(List<Long> sorted, double p) {
    if (sorted == null || sorted.isEmpty()) {
      return 0;
    }
    int idx = (int) Math.ceil(p * sorted.size()) - 1;
    idx = Math.max(0, idx);
    return sorted.get(idx).intValue();
  }

  @Override
  public RealtimeStatusCodeDistributionVo getRealtimeStatusCodeDistribution(String period) {
    return new BizTemplate<RealtimeStatusCodeDistributionVo>() {
      @Override
      protected RealtimeStatusCodeDistributionVo process() {
        LocalDateTime[] periodRange = parsePeriod(period);
        LocalDateTime startDate = periodRange[0];
        LocalDateTime endDate = periodRange[1];

        List<Object[]> rows =
            interfaceRequestLogInfoRepo.countByStatusRangeAndDateRange(startDate, endDate, null);

        Map<String, Long> distribution = new LinkedHashMap<>();
        for (Object[] row : rows) {
          distribution.put((String) row[0], ((Number) row[1]).longValue());
        }

        RealtimeStatusCodeDistributionVo vo = new RealtimeStatusCodeDistributionVo();
        vo.setDistribution(distribution);
        return vo;
      }
    }.execute();
  }

  @Override
  public List<TopCallsVo> getTopCalls(Integer limit, String period) {
    return new BizTemplate<List<TopCallsVo>>() {
      @Override
      protected List<TopCallsVo> process() {
        LocalDateTime[] periodRange = parsePeriod(period);
        LocalDateTime startDate = periodRange[0];
        LocalDateTime endDate = periodRange[1];

        List<Object[]> topCalls = interfaceRequestLogInfoRepo.findTopCalls(startDate, endDate,
            null);

        int maxSize = limit != null && limit > 0 ? limit : 10;
        List<TopCallsVo> voList = new ArrayList<>();
        for (int i = 0; i < Math.min(maxSize, topCalls.size()); i++) {
          Object[] row = topCalls.get(i);
          TopCallsVo vo = new TopCallsVo();
          vo.setServiceName((String) row[0]);
          vo.setUri((String) row[1]);
          vo.setMethod((String) row[2]);
          vo.setCalls(((Number) row[3]).longValue());
          vo.setErrorRate(((Number) row[4]).doubleValue());
          voList.add(vo);
        }

        return voList;
      }
    }.execute();
  }

  @Override
  public List<TopSlowVo> getTopSlow(Integer limit, String period) {
    return new BizTemplate<List<TopSlowVo>>() {
      @Override
      protected List<TopSlowVo> process() {
        LocalDateTime[] periodRange = parsePeriod(period);
        LocalDateTime startDate = periodRange[0];
        LocalDateTime endDate = periodRange[1];

        List<Object[]> topSlow = interfaceRequestLogInfoRepo.findTopSlow(startDate, endDate, null);

        int maxSize = limit != null && limit > 0 ? limit : 10;
        List<TopSlowVo> voList = new ArrayList<>();
        for (int i = 0; i < Math.min(maxSize, topSlow.size()); i++) {
          Object[] row = topSlow.get(i);
          TopSlowVo vo = new TopSlowVo();
          vo.setServiceName((String) row[0]);
          vo.setUri((String) row[1]);
          vo.setMethod((String) row[2]);
          vo.setAvgResponseTime(((Number) row[3]).intValue());
          vo.setCalls(((Number) row[4]).longValue());
          voList.add(vo);
        }

        return voList;
      }
    }.execute();
  }

  @Override
  public List<TopErrorsVo> getTopErrors(Integer limit, String period) {
    return new BizTemplate<List<TopErrorsVo>>() {
      @Override
      protected List<TopErrorsVo> process() {
        LocalDateTime[] periodRange = parsePeriod(period);
        LocalDateTime startDate = periodRange[0];
        LocalDateTime endDate = periodRange[1];

        List<Object[]> topErrors = interfaceRequestLogInfoRepo.findTopErrors(startDate, endDate,
            null);

        int maxSize = limit != null && limit > 0 ? limit : 10;
        List<TopErrorsVo> voList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (int i = 0; i < Math.min(maxSize, topErrors.size()); i++) {
          Object[] row = topErrors.get(i);
          String serviceName = (String) row[0];
          String uri = (String) row[1];
          String method = (String) row[2];

          TopErrorsVo vo = new TopErrorsVo();
          vo.setServiceName(serviceName);
          vo.setUri(uri);
          vo.setMethod(method);
          vo.setErrorRate(((Number) row[3]).doubleValue());
          vo.setCalls(((Number) row[4]).longValue());
          vo.setFailedCalls(((Number) row[5]).longValue());

          List<InterfaceRequestLog> lastErrors = interfaceRequestLogRepo.findLastErrorByInterface(
              serviceName, uri, method, startDate, endDate, PageRequest.of(0, 1));
          if (!lastErrors.isEmpty()) {
            InterfaceRequestLog lastError = lastErrors.get(0);
            vo.setLastErrorTime(lastError.getRequestDate());
            String message = extractMessageFromResponseBody(objectMapper,
                lastError.getResponseBody());
            vo.setLastError(message);
          }

          voList.add(vo);
        }

        return voList;
      }
    }.execute();
  }

  /**
   * 从 responseBody JSON 中提取第一层 message 字段
   */
  private static String extractMessageFromResponseBody(ObjectMapper objectMapper,
      String responseBody) {
    if (!StringUtils.hasText(responseBody)) {
      return null;
    }
    try {
      JsonNode root = objectMapper.readTree(responseBody);
      if (root != null && root.isObject() && root.has("message")) {
        JsonNode messageNode = root.get("message");
        return messageNode != null && !messageNode.isNull() ? messageNode.asText() : null;
      }
    } catch (Exception ignored) {
      // 解析失败时返回 null
    }
    return null;
  }
}
