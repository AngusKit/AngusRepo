package cloud.xcan.angus.core.gm.application.query.log.impl;

import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.formatFileSize;

import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.commonlink.application.ApplicationRepo;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.log.SystemLogQuery;
import cloud.xcan.angus.core.gm.domain.log.SystemLog;
import cloud.xcan.angus.core.gm.domain.log.SystemLogRepo;
import cloud.xcan.angus.core.gm.domain.log.SystemLogSearchRepo;
import cloud.xcan.angus.core.gm.infra.log.SystemLogFileService;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogContentDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogStatisticsDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.SystemLogContentVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.SystemLogStatisticsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 系统日志查询服务实现
 */
@Service
public class SystemLogQueryImpl implements SystemLogQuery {

  @Resource
  private SystemLogRepo systemLogRepo;

  @Resource
  private SystemLogSearchRepo systemLogSearchRepo;

  @Resource
  private SystemLogFileService systemLogFileService;

  @Resource
  private ApplicationRepo applicationRepo;

  @Resource
  private ApplicationInfo applicationInfo;

  @Override
  public SystemLog findAndCheck(Long id) {
    return new BizTemplate<SystemLog>() {
      @Override
      protected SystemLog process() {
        return systemLogRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("系统日志「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Page<SystemLog> find(GenericSpecification<SystemLog> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<SystemLog>>() {
      @Override
      protected Page<SystemLog> process() {
        return fullTextSearch
            ? systemLogSearchRepo.find(spec.getCriteria(), pageable, SystemLog.class, match)
            : systemLogRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public SystemLogContentVo getContent(Long id, SystemLogContentDto dto) {
    return new BizTemplate<SystemLogContentVo>() {
      SystemLog systemLog;

      @Override
      protected void checkParams() {
        systemLog = findAndCheck(id);
      }

      @Override
      protected SystemLogContentVo process() {
        // 如果读取实例IP和当前系统IP不一致，转发对应实例进行进行读取
        String currentInstance = applicationInfo.getInstanceId(); // 格式：IP:PORT
        String currentInstanceIp = currentInstance.split(":")[0];
        String targetInstanceId = systemLog.getInstanceId();
        if (targetInstanceId == null || targetInstanceId.isEmpty()) {
          // 如果没有实例ID，直接本地读取
          try {
            return systemLogFileService.readLogContent(systemLog, dto);
          } catch (IOException e) {
            throw new RuntimeException("读取日志文件失败: " + e.getMessage(), e);
          }
        }

        // 需要转发到目标实例
        String targetInstanceIp = targetInstanceId.split(":")[0];
        if (!currentInstanceIp.equals(targetInstanceIp)) {
          return forwardToTargetInstance(targetInstanceId, id, dto);
        }

        // 本地读取
        try {
          return systemLogFileService.readLogContent(systemLog, dto);
        } catch (IOException e) {
          throw new RuntimeException("读取日志文件失败: " + e.getMessage(), e);
        }
      }
    }.execute();
  }

  @Override
  public SystemLogStatisticsVo getStatistics(SystemLogStatisticsDto dto) {
    return new BizTemplate<SystemLogStatisticsVo>() {
      @Override
      protected SystemLogStatisticsVo process() {
        SystemLogStatisticsVo stats = new SystemLogStatisticsVo();

        // 设置时间范围
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        if (endDate == null) {
          endDate = LocalDate.now();
        }
        if (startDate == null) {
          startDate = endDate.minusDays(30);
        }

        Long applicationId = dto.getApplicationId();

        // 统计总文件数和总大小
        List<SystemLog> allLogs = systemLogRepo.findByDateBetween(startDate, endDate);
        if (applicationId != null) {
          allLogs = allLogs.stream()
              .filter(log -> applicationId.equals(log.getApplicationId()))
              .collect(Collectors.toList());
        }

        int totalFiles = allLogs.size();
        long totalSize = allLogs.stream().mapToLong(SystemLog::getSize).sum();
        stats.setTotalFiles(totalFiles);
        stats.setTotalSize(totalSize);
        stats.setTotalSizeFormatted(formatFileSize(totalSize));

        // 统计各类型的数量和大小
        List<Object[]> typeStats = systemLogRepo.countAndSizeByTypeAndDateRange(startDate, endDate,
            applicationId);
        Map<String, SystemLogStatisticsVo.TypeStatisticsVo> typeStatistics = new HashMap<>();
        for (Object[] result : typeStats) {
          cloud.xcan.angus.core.gm.domain.log.enums.LogType type =
              (cloud.xcan.angus.core.gm.domain.log.enums.LogType) result[0];
          Integer count = ((Long) result[1]).intValue();
          Long size = (Long) result[2];

          SystemLogStatisticsVo.TypeStatisticsVo typeStat =
              new SystemLogStatisticsVo.TypeStatisticsVo();
          typeStat.setCount(count);
          typeStat.setSize(size);
          typeStat.setSizeFormatted(formatFileSize(size));
          typeStatistics.put(type.name(), typeStat);
        }
        stats.setTypeStatistics(typeStatistics);

        // 统计各状态的数量
        List<Object[]> statusStats = systemLogRepo.countByStatusAndDateRange(startDate, endDate,
            applicationId);
        Map<String, Integer> statusStatistics = new HashMap<>();
        for (Object[] result : statusStats) {
          cloud.xcan.angus.core.gm.domain.log.enums.LogStatus status =
              (cloud.xcan.angus.core.gm.domain.log.enums.LogStatus) result[0];
          Integer count = ((Long) result[1]).intValue();
          statusStatistics.put(status.name(), count);
        }
        stats.setStatusStatistics(statusStatistics);

        // 统计各应用的数量和大小
        List<Object[]> appStats = systemLogRepo.countAndSizeByApplicationAndDateRange(startDate,
            endDate, applicationId);

        // 批量查询应用信息以填充应用名称
        List<Long> appIds = appStats.stream()
            .map(result -> (Long) result[0])
            .distinct()
            .collect(Collectors.toList());

        Map<Long, String> appNameMap = new HashMap<>();
        if (!appIds.isEmpty()) {
          // 通过code查询应用（假设applicationId是应用的code）
          Set<SearchCriteria> appFilters = new HashSet<>();
          appFilters.add(new SearchCriteria("code", appIds, SearchOperation.IN));
          GenericSpecification<Application> appSpec = new GenericSpecification<>(appFilters);
          List<Application> applications = applicationRepo.findAll(appSpec);
          appNameMap = applications.stream().collect(
              Collectors.toMap(Application::getId, Application::getName,
                  (existing, replacement) -> existing));
        }

        Map<Long, String> finalAppNameMap = appNameMap;
        List<SystemLogStatisticsVo.ApplicationStatisticsVo> applicationStatistics =
            appStats.stream()
                .map(result -> {
                  SystemLogStatisticsVo.ApplicationStatisticsVo appStat =
                      new SystemLogStatisticsVo.ApplicationStatisticsVo();
                  Long appId = (Long) result[0];
                  appStat.setApplicationId(appId);
                  appStat.setApplicationName(finalAppNameMap.getOrDefault(appId, ""));
                  appStat.setFileCount(((Long) result[1]).intValue());
                  appStat.setTotalSize((Long) result[2]);
                  appStat.setTotalSizeFormatted(formatFileSize((Long) result[2]));
                  return appStat;
                })
                .collect(Collectors.toList());
        stats.setApplicationStatistics(applicationStatistics);

        // 查询最早和最新的日志
        List<SystemLog> oldestLogs = systemLogRepo.findOldestLog(startDate, endDate, applicationId);
        if (!oldestLogs.isEmpty()) {
          SystemLog oldest = oldestLogs.get(0);
          SystemLogStatisticsVo.LogFileInfoVo oldestInfo =
              new SystemLogStatisticsVo.LogFileInfoVo();
          oldestInfo.setFilename(oldest.getFilename());
          oldestInfo.setDate(oldest.getDate().toString());
          stats.setOldestLog(oldestInfo);
        }

        List<SystemLog> newestLogs = systemLogRepo.findNewestLog(startDate, endDate, applicationId);
        if (!newestLogs.isEmpty()) {
          SystemLog newest = newestLogs.get(0);
          SystemLogStatisticsVo.LogFileInfoVo newestInfo =
              new SystemLogStatisticsVo.LogFileInfoVo();
          newestInfo.setFilename(newest.getFilename());
          newestInfo.setDate(newest.getDate().toString());
          stats.setNewestLog(newestInfo);
        }
        return stats;
      }
    }.execute();
  }


  /**
   * 转发请求到目标实例
   */
  private SystemLogContentVo forwardToTargetInstance(String targetInstanceId, Long logId,
      SystemLogContentDto dto) {
    try {
      // 构建目标URL
      String baseUrl = "http://" + targetInstanceId;
      String url = baseUrl + "/api/v1/logs/system/" + logId + "/content";

      // 构建查询参数
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
      if (dto.getPage() != null) {
        uriBuilder.queryParam("page", dto.getPage());
      }
      if (dto.getSize() != null) {
        uriBuilder.queryParam("size", dto.getSize());
      }
      if (StringUtils.hasText(dto.getKeyword())) {
        uriBuilder.queryParam("keyword", dto.getKeyword());
      }
      if (dto.getLevel() != null) {
        uriBuilder.queryParam("level", dto.getLevel().name());
      }
      if (dto.getStartLine() != null) {
        uriBuilder.queryParam("startLine", dto.getStartLine());
      }
      if (dto.getEndLine() != null) {
        uriBuilder.queryParam("endLine", dto.getEndLine());
      }
      if (Boolean.TRUE.equals(dto.getTail())) {
        uriBuilder.queryParam("tail", true);
      }
      if (dto.getTailLines() != null) {
        uriBuilder.queryParam("tailLines", dto.getTailLines());
      }
      String finalUrl = uriBuilder.toUriString();

      // 创建RestTemplate
      SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
      factory.setConnectTimeout(5000);
      factory.setReadTimeout(30000); // 日志读取可能需要较长时间
      RestTemplate restTemplate = new RestTemplate(factory);

      // 设置请求头
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
      String authorization = PrincipalContext.getAuthorization();
      if (StringUtils.hasText(authorization)) {
        headers.set("Authorization", authorization);
      }

      // 发送GET请求
      HttpEntity<String> entity = new HttpEntity<>(headers);
      ResponseEntity<ApiLocaleResult<SystemLogContentVo>> response
          = restTemplate.exchange(finalUrl, HttpMethod.GET, entity,
          new ParameterizedTypeReference<ApiLocaleResult<SystemLogContentVo>>() {
          });

      if (response.getStatusCode().is2xxSuccessful()) {
        ApiLocaleResult<SystemLogContentVo> result = response.getBody();
        // 检查业务状态码
        if ("S".equals(result.getCode()) && result.getData() != null) {
          return result.getData();
        } else {
          String errorMsg = result.getMessage();
          throw new SysException("转发请求失败: " + errorMsg);
        }
      } else {
        throw new SysException(
            "转发请求失败: HTTP状态码 " + response.getStatusCode().value());
      }
    } catch (RestClientException e) {
      throw new SysException("转发请求到实例 " + targetInstanceId
          + " 失败: " + e.getMessage(), e);
    }
  }
}
