package cloud.xcan.angus.core.gm.application.query.interfaces.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.interfaces.InterfaceRequestLogQuery;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLog;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLogInfo;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLogInfoRepo;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLogRepo;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLogSearchRepo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceCallStatsDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceRequestLogStatisticsDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceCallStatsVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.InterfaceRequestLogStatisticsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.http.HttpMethod;
import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * API请求日志查询服务实现
 */
@Service
@Transactional(readOnly = true)
public class InterfaceRequestLogQueryImpl implements InterfaceRequestLogQuery {

  @Resource
  private InterfaceRequestLogRepo interfaceRequestLogRepo;

  @Resource
  private InterfaceRequestLogInfoRepo interfaceRequestLogInfoRepo;

  @Resource
  private InterfaceRequestLogSearchRepo interfaceRequestLogSearchRepo;

  @Override
  public InterfaceRequestLog findAndCheck(Long id) {
    return new BizTemplate<InterfaceRequestLog>() {
      @Override
      protected InterfaceRequestLog process() {
        return interfaceRequestLogRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("API请求日志「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Page<InterfaceRequestLogInfo> find(GenericSpecification<InterfaceRequestLogInfo> spec,
      PageRequest pageable, boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<InterfaceRequestLogInfo>>() {
      @Override
      protected Page<InterfaceRequestLogInfo> process() {
        return fullTextSearch
            ? interfaceRequestLogSearchRepo.find(spec.getCriteria(), pageable,
            InterfaceRequestLogInfo.class, match)
            : interfaceRequestLogInfoRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public InterfaceRequestLogStatisticsVo getStatistics(InterfaceRequestLogStatisticsDto dto) {
    return new BizTemplate<InterfaceRequestLogStatisticsVo>() {
      @Override
      protected InterfaceRequestLogStatisticsVo process() {
        InterfaceRequestLogStatisticsVo stats = new InterfaceRequestLogStatisticsVo();

        // 设置时间范围
        LocalDateTime startDate = dto.getStartDate();
        LocalDateTime endDate = dto.getEndDate();
        if (endDate == null) {
          endDate = LocalDateTime.now();
        }
        if (startDate == null) {
          startDate = endDate.minusDays(30);
        }

        String applicationCode = dto.getApplicationCode();

        // 统计总请求次数
        long totalCount = interfaceRequestLogInfoRepo.countByDateRange(startDate, endDate,
            applicationCode);
        stats.setTotalCount(totalCount);

        // 统计成功和失败请求次数
        long successCount = interfaceRequestLogInfoRepo.countSuccessByDateRange(startDate, endDate,
            applicationCode);
        long errorCount = interfaceRequestLogInfoRepo.countErrorByDateRange(startDate, endDate,
            applicationCode);
        stats.setSuccessCount(successCount);
        stats.setErrorCount(errorCount);

        // 计算成功率
        if (totalCount > 0) {
          stats.setSuccessRate((double) successCount / totalCount * 100);
        } else {
          stats.setSuccessRate(0.0);
        }

        // 计算平均响应时间
        Double avgResponseTime = interfaceRequestLogInfoRepo.avgResponseTimeByDateRange(startDate,
            endDate, applicationCode);
        stats.setAvgResponseTime(avgResponseTime != null ? avgResponseTime : 0.0);

        // 统计各请求方法的数量
        List<Object[]> methodStats = interfaceRequestLogInfoRepo.countByMethodAndDateRange(
            startDate, endDate, applicationCode);
        Map<String, Long> methodStatistics = new HashMap<>();
        for (Object[] result : methodStats) {
          HttpMethod method = (HttpMethod) result[0];
          Long count = (Long) result[1];
          methodStatistics.put(method.name(), count);
        }
        stats.setMethodStatistics(methodStatistics);

        // 统计各状态码范围的数量
        List<Object[]> statusStats = interfaceRequestLogInfoRepo.countByStatusRangeAndDateRange(
            startDate, endDate, applicationCode);
        Map<String, Long> statusStatistics = new HashMap<>();
        for (Object[] result : statusStats) {
          String statusRange = (String) result[0];
          Long count = (Long) result[1];
          statusStatistics.put(statusRange, count);
        }
        stats.setStatusStatistics(statusStatistics);

        // 查询请求最频繁的端点TOP10
        List<Object[]> topEndpointsData = interfaceRequestLogInfoRepo.findTopEndpointsByRequestCount(
            startDate, endDate, applicationCode);
        List<InterfaceRequestLogStatisticsVo.TopEndpointVo> topEndpoints = topEndpointsData.stream()
            .limit(10)
            .map(result -> {
              InterfaceRequestLogStatisticsVo.TopEndpointVo topEndpoint =
                  new InterfaceRequestLogStatisticsVo.TopEndpointVo();
              topEndpoint.setEndpoint((String) result[0]);
              topEndpoint.setCount((Long) result[1]);
              topEndpoint.setAvgResponseTime(((Double) result[2]));
              return topEndpoint;
            })
            .collect(Collectors.toList());
        stats.setTopEndpoints(topEndpoints);

        // 查询请求最频繁的API密钥TOP10
        List<Object[]> topApiKeysData = interfaceRequestLogInfoRepo.findTopApiKeysByRequestCount(
            startDate, endDate, applicationCode);
        List<InterfaceRequestLogStatisticsVo.TopApiKeyVo> topApiKeys = topApiKeysData.stream()
            .limit(10)
            .map(result -> {
              InterfaceRequestLogStatisticsVo.TopApiKeyVo topApiKey =
                  new InterfaceRequestLogStatisticsVo.TopApiKeyVo();
              topApiKey.setApiKeyId((String) result[0]);
              topApiKey.setApiKey((String) result[1]);
              topApiKey.setCount((Long) result[2]);
              return topApiKey;
            })
            .collect(Collectors.toList());
        stats.setTopApiKeys(topApiKeys);

        return stats;
      }
    }.execute();
  }

  @Override
  public InterfaceCallStatsVo getCallStats(String serviceName, String uri, String method,
      InterfaceCallStatsDto dto) {
    return new BizTemplate<InterfaceCallStatsVo>() {
      @Override
      protected InterfaceCallStatsVo process() {
        // 解析时间范围
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        if (endDate == null) {
          endDate = LocalDate.now();
        }
        if (startDate == null) {
          startDate = endDate.minusDays(30);
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // 构建返回对象
        InterfaceCallStatsVo vo = new InterfaceCallStatsVo();
        vo.setPath(uri);
        vo.setMethod(method);

        // 设置时间周期
        InterfaceCallStatsVo.Period period = new InterfaceCallStatsVo.Period();
        period.setStartDate(startDate);
        period.setEndDate(endDate);
        vo.setPeriod(period);

        // 统计总请求数
        long totalRequests = interfaceRequestLogInfoRepo.countByInterface(
            serviceName, uri, method, startDateTime, endDateTime, null);
        vo.setTotalRequests(totalRequests);

        // 统计成功请求数
        long successRequests = interfaceRequestLogInfoRepo.countSuccessByInterface(
            serviceName, uri, method, startDateTime, endDateTime, null);
        vo.setSuccessRequests(successRequests);

        // 统计失败请求数
        long failedRequests = interfaceRequestLogInfoRepo.countFailedByInterface(
            serviceName, uri, method, startDateTime, endDateTime, null);
        vo.setFailedRequests(failedRequests);

        // 计算平均响应时间
        Double avgResponseTime = interfaceRequestLogInfoRepo.avgResponseTimeByInterface(
            serviceName, uri, method, startDateTime, endDateTime, null);
        vo.setAvgResponseTime(avgResponseTime != null ? avgResponseTime.intValue() : null);

        // 获取最大响应时间
        Long maxResponseTime = interfaceRequestLogInfoRepo.maxResponseTimeByInterface(
            serviceName, uri, method, startDateTime, endDateTime, null);
        vo.setMaxResponseTime(maxResponseTime != null ? maxResponseTime.intValue() : null);

        // 获取最小响应时间
        Long minResponseTime = interfaceRequestLogInfoRepo.minResponseTimeByInterface(
            serviceName, uri, method, startDateTime, endDateTime, null);
        vo.setMinResponseTime(minResponseTime != null ? minResponseTime.intValue() : null);

        // 按天统计请求数
        List<Object[]> dailyCounts = interfaceRequestLogInfoRepo.countDailyByInterface(
            serviceName, uri, method, startDateTime, endDateTime, null);
        List<InterfaceCallStatsVo.DailyCount> requestsPerDay = dailyCounts.stream()
            .map(result -> {
              InterfaceCallStatsVo.DailyCount dailyCount = new InterfaceCallStatsVo.DailyCount();
              // result[0] 是 java.sql.Date，需要转换为 LocalDate
              if (result[0] instanceof java.sql.Date) {
                dailyCount.setDate(((java.sql.Date) result[0]).toLocalDate());
              } else if (result[0] instanceof LocalDate) {
                dailyCount.setDate((LocalDate) result[0]);
              }
              dailyCount.setCount(((Number) result[1]).longValue());
              return dailyCount;
            })
            .collect(Collectors.toList());
        vo.setRequestsPerDay(requestsPerDay);

        // 统计错误码
        List<Object[]> errorCodesData = interfaceRequestLogInfoRepo.countErrorCodesByInterface(
            serviceName, uri, method, startDateTime, endDateTime, null);
        List<InterfaceCallStatsVo.ErrorCode> errorCodes = errorCodesData.stream()
            .map(result -> {
              InterfaceCallStatsVo.ErrorCode errorCode = new InterfaceCallStatsVo.ErrorCode();
              errorCode.setCode(((Number) result[0]).intValue());
              errorCode.setCount(((Number) result[1]).longValue());
              return errorCode;
            })
            .collect(Collectors.toList());
        vo.setErrorCodes(errorCodes);

        return vo;
      }
    }.execute();
  }
}
