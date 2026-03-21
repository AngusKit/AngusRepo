package cloud.xcan.angus.core.gm.application.query.dashboard.impl;

import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.formatFileSize;

import cloud.xcan.angus.api.commonlink.TrendEnum;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.commonlink.user.enums.UserStatus;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.dashboard.DashboardQuery;
import cloud.xcan.angus.core.gm.application.query.system.SystemMonitoringQuery;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.domain.log.UserOperationLog;
import cloud.xcan.angus.core.gm.domain.log.UserOperationLogRepo;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.log.enums.ResponseStatus;
import cloud.xcan.angus.core.gm.domain.notification.NotificationRepo;
import cloud.xcan.angus.core.gm.domain.sms.SmsRepo;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo.DashboardStatisticsVo;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo.RecentActivitiesVo;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo.SystemResourcesVo;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo.UserGrowthVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.CpuUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.DiskUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.EnvironmentVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.MemoryUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.MonitoringOverviewVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.NetworkUsageVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatsVo;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardQueryImpl implements DashboardQuery {

  @Resource
  private TenantQuery tenantQuery;

  @Resource
  private SystemMonitoringQuery systemMonitoringQuery;

  @Resource
  private UserRepo userRepo;

  @Resource
  private NotificationRepo notificationRepo;

  @Resource
  private SmsRepo smsRepo;

  @Resource
  private UserOperationLogRepo userOperationLogRepo;

  @Override
  public DashboardStatisticsVo getStatistics() {
    return new BizTemplate<DashboardStatisticsVo>(false) {
      @Override
      protected DashboardStatisticsVo process() {
        List<Long> tenantIds = tenantQuery.getTenantIdsBySameAccount();
        DashboardStatisticsVo vo = new DashboardStatisticsVo();

        // 1. 用户统计数据（同账号所有租户）
        vo.setUserStats(buildUserStats(tenantIds));

        // 2. 租户统计数据（同账号所有租户，tenantQuery.getStats 已按同账号范围统计）
        vo.setTenantStats(buildTenantStats());

        // 3. 操作统计数据（同账号所有租户）
        vo.setOperationStats(buildOperationStats(tenantIds));

        // 4. 通知统计数据（同账号所有租户）
        vo.setNotificationStats(buildNotificationStats(tenantIds));

        return vo;
      }
    }.execute();
  }

  @Override
  public UserGrowthVo getUserGrowth(String timeRange) {
    return new BizTemplate<UserGrowthVo>(false) {
      @Override
      protected UserGrowthVo process() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        int dataPointCount = switch (timeRange) {
          case "7DAYS" -> {
            startDate = now.minusDays(7);
            yield 8; // 7天前至当天共8个数据点，包含当天
          }
          case "30DAYS" -> {
            startDate = now.minusDays(30);
            yield 31; // 30天前至当天共31个数据点，包含当天
          }
          case "90DAYS" -> {
            startDate = now.minusDays(90);
            yield 13; // 13周覆盖90天+当天，包含今天
          }
          case "1YEAR" -> {
            startDate = now.minusYears(1);
            yield 13; // 13个月包含当前月（今天所在月）
          }
          case "ALL" -> {
            // 查询所有数据，按月统计，默认从1年前开始
            startDate = now.minusYears(1);
            yield 13; // 13个月包含当前月（今天所在月）
          }
          default -> {
            startDate = now.minusMonths(6);
            yield 7; // 7个月包含当前月（今天所在月）
          }
        };

        // 根据时间范围确定起始日期和数据点数量

        // 查询用户增长数据
        List<UserGrowthVo.GrowthDataPointVo> dataPoints = queryUserGrowthData(startDate, now,
            timeRange, dataPointCount);

        // 计算总增长数和增长率
        long totalGrowth = 0;
        String growthRate = "0%";
        if (!dataPoints.isEmpty()) {
          long startCount = dataPoints.get(0).getUserCount();
          long endCount = dataPoints.get(dataPoints.size() - 1).getUserCount();
          totalGrowth = endCount - startCount;
          if (startCount > 0) {
            double rate = ((double) totalGrowth / startCount) * 100;
            growthRate = String.format("%s%.1f%%", totalGrowth >= 0 ? "+" : "", rate);
          }
        }

        UserGrowthVo vo = new UserGrowthVo();
        vo.setTimeRange(timeRange);
        vo.setDataPoints(dataPoints);
        vo.setTotalGrowth(totalGrowth);
        vo.setGrowthRate(growthRate);
        return vo;
      }
    }.execute();
  }

  @Override
  public SystemResourcesVo getSystemResources() {
    return new BizTemplate<SystemResourcesVo>() {
      @Override
      protected SystemResourcesVo process() {
        MonitoringOverviewVo overview = systemMonitoringQuery.getOverview();
        EnvironmentVo environment = systemMonitoringQuery.getEnvironment();
        CpuUsageVo cpuUsage = systemMonitoringQuery.getCpuUsage("1h");
        MemoryUsageVo memoryUsage = systemMonitoringQuery.getMemoryUsage("1h");
        DiskUsageVo diskUsage = systemMonitoringQuery.getDiskUsage();
        NetworkUsageVo networkUsage = systemMonitoringQuery.getNetworkUsage("1h");

        SystemResourcesVo vo = new SystemResourcesVo();
        vo.setCollectedAt(LocalDateTime.now());

        // CPU
        SystemResourcesVo.ResourceUsageVo cpu = new SystemResourcesVo.ResourceUsageVo();
        cpu.setLabel("CPU使用率");
        Double cpuUsagePercent = overview.getCpuUsage();
        cpu.setUsagePercent(cpuUsagePercent != null ? cpuUsagePercent.intValue() : 0);

        // 从 EnvironmentVo 获取 CPU 核心数
        Integer cpuCores = environment.getCpu() != null ? environment.getCpu().getCores() : null;
        if (cpuCores == null && cpuUsage != null && cpuUsage.getCores() != null) {
          cpuCores = cpuUsage.getCores();
        }
        if (cpuCores == null) {
          cpuCores = 1; // 默认值
        }
        cpu.setTotal(cpuCores + "核");

        // 计算当前使用的核心数
        double currentCores = cpuUsagePercent != null && cpuCores > 0
            ? cpuUsagePercent * cpuCores / 100.0 : 0;
        cpu.setCurrent(String.format("%.1f核", currentCores));
        vo.setCpu(cpu);

        // Memory
        SystemResourcesVo.ResourceUsageVo memory = new SystemResourcesVo.ResourceUsageVo();
        memory.setLabel("内存使用率");
        Double memoryUsagePercent = overview.getMemoryUsage();
        memory.setUsagePercent(memoryUsagePercent != null ? memoryUsagePercent.intValue() : 0);

        // 从 MemoryUsageVo 获取内存总量和已用量
        String memoryTotal = memoryUsage != null ? memoryUsage.getTotal() : null;
        String memoryUsed = memoryUsage != null ? memoryUsage.getUsed() : null;
        if (memoryTotal == null && environment.getMemory() != null) {
          memoryTotal = environment.getMemory().getTotal();
        }
        if (memoryUsed == null && environment.getMemory() != null) {
          memoryUsed = environment.getMemory().getUsed();
        }
        memory.setTotal(memoryTotal != null ? memoryTotal : "0 GB");
        memory.setCurrent(memoryUsed != null ? memoryUsed : "0 GB");
        vo.setMemory(memory);

        // Disk
        SystemResourcesVo.ResourceUsageVo disk = new SystemResourcesVo.ResourceUsageVo();
        disk.setLabel("磁盘使用率");
        String diskTotal = null;
        String diskUsed = null;
        long totalBytes = 0;
        long usedBytes = 0;
        if (diskUsage != null && diskUsage.getDisks() != null && !diskUsage.getDisks().isEmpty()) {
          // 汇总所有磁盘，并基于汇总数据计算使用率（保证百分比与 total/used 一致）
          for (DiskUsageVo.DiskInfo diskInfo : diskUsage.getDisks()) {
            totalBytes += parseFileSize(diskInfo.getTotal());
            usedBytes += parseFileSize(diskInfo.getUsed());
          }
          diskTotal = formatFileSize(totalBytes);
          diskUsed = formatFileSize(usedBytes);
        } else if (environment.getDisk() != null) {
          diskTotal = environment.getDisk().getTotal();
          totalBytes = parseFileSize(diskTotal);
          long freeBytes = parseFileSize(environment.getDisk().getFree());
          usedBytes = totalBytes - freeBytes;
          diskUsed = formatFileSize(usedBytes);
        }
        disk.setTotal(diskTotal != null ? diskTotal : "0 GB");
        disk.setCurrent(diskUsed != null ? diskUsed : "0 GB");
        disk.setUsagePercent(
            totalBytes > 0 ? (int) Math.round((double) usedBytes / totalBytes * 100) : 0);
        vo.setDisk(disk);

        // Network
        SystemResourcesVo.ResourceUsageVo network = new SystemResourcesVo.ResourceUsageVo();
        network.setLabel("网络带宽");

        // currentInRate/OutRate 格式为 "XX.XX MB/s" 或 "XX.XX GB/s"，parseFileSize 解析为 bytes
        long currentBytesPerSec = 0; // 当前速率（bytes/s）
        long totalBandwidthBps = 0;  // 总带宽（bits/s），每接口默认 1Gbps（NetworkUsageVo 未提供 speed）

        if (networkUsage != null && networkUsage.getInterfaces() != null) {
          for (NetworkUsageVo.NetworkInterface netIF : networkUsage.getInterfaces()) {
            String inRate = netIF.getCurrentInRate();
            String outRate = netIF.getCurrentOutRate();
            if (inRate != null && inRate.endsWith("/s")) {
              currentBytesPerSec += parseFileSize(inRate.substring(0, inRate.length() - 2).trim());
            }
            if (outRate != null && outRate.endsWith("/s")) {
              currentBytesPerSec += parseFileSize(
                  outRate.substring(0, outRate.length() - 2).trim());
            }
            totalBandwidthBps += 1000L * 1000 * 1000; // 1Gbps
          }
        }
        if (totalBandwidthBps == 0) {
          totalBandwidthBps = 1000L * 1000 * 1000; // 默认 1Gbps
        }

        // 使用率：current 为 bytes/s，total 为 bps，需统一单位 (bytes*8 = bits)
        int usagePercent = totalBandwidthBps > 0
            ? (int) Math.min(100, (currentBytesPerSec * 8.0 * 100) / totalBandwidthBps)
            : 0;
        network.setUsagePercent(usagePercent);

        if (totalBandwidthBps >= 1000L * 1000 * 1000) {
          network.setTotal(String.format("%.0fGbps", totalBandwidthBps / (1000.0 * 1000 * 1000)));
        } else if (totalBandwidthBps >= 1000L * 1000) {
          network.setTotal(String.format("%.0fMbps", totalBandwidthBps / (1000.0 * 1000)));
        } else {
          network.setTotal(String.format("%.0fKbps", totalBandwidthBps / 1000.0));
        }
        network.setCurrent(formatFileSize(currentBytesPerSec) + "/s");
        vo.setNetwork(network);

        return vo;
      }
    }.execute();
  }

  /**
   * 解析文件大小字符串为字节数（简化实现） 支持格式：XX.XX B, XX.XX KB, XX.XX MB, XX.XX GB, XX.XX TB
   */
  private long parseFileSize(String sizeStr) {
    if (sizeStr == null || sizeStr.isEmpty()) {
      return 0L;
    }
    try {
      sizeStr = sizeStr.trim().toUpperCase();
      if (sizeStr.endsWith("B")) {
        sizeStr = sizeStr.substring(0, sizeStr.length() - 1).trim();
        if (sizeStr.endsWith("K")) {
          double value = Double.parseDouble(sizeStr.substring(0, sizeStr.length() - 1).trim());
          return (long) (value * 1024);
        } else if (sizeStr.endsWith("M")) {
          double value = Double.parseDouble(sizeStr.substring(0, sizeStr.length() - 1).trim());
          return (long) (value * 1024 * 1024);
        } else if (sizeStr.endsWith("G")) {
          double value = Double.parseDouble(sizeStr.substring(0, sizeStr.length() - 1).trim());
          return (long) (value * 1024 * 1024 * 1024);
        } else if (sizeStr.endsWith("T")) {
          double value = Double.parseDouble(sizeStr.substring(0, sizeStr.length() - 1).trim());
          return (long) (value * 1024L * 1024L * 1024L * 1024L);
        } else {
          return Long.parseLong(sizeStr);
        }
      }
      return Long.parseLong(sizeStr);
    } catch (NumberFormatException e) {
      return 0L;
    }
  }

  @Override
  public RecentActivitiesVo getRecentActivities(Integer limit, ResourceType resourceType) {
    return new BizTemplate<RecentActivitiesVo>(false) {
      @Override
      protected RecentActivitiesVo process() {
        List<Long> tenantIds = tenantQuery.getTenantIdsBySameAccount();
        if (tenantIds.isEmpty()) {
          RecentActivitiesVo vo = new RecentActivitiesVo();
          vo.setActivities(List.of());
          vo.setTotal(0L);
          return vo;
        }

        Pageable pageable = PageRequest.of(0, limit != null ? limit : 10,
            Sort.by(Sort.Direction.DESC, "createdDate"));

        List<UserOperationLog> logs = userOperationLogRepo.findRecentLogsByTenantIdIn(
            tenantIds, resourceType, pageable);

        DashboardQueryImpl outer = DashboardQueryImpl.this;
        List<RecentActivitiesVo.ActivityVo> activities = logs.stream()
            .map(outer::convertToActivityVo)
            .collect(Collectors.toList());

        long total;
        LocalDateTime allTimeStart = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        if (resourceType != null) {
          total = userOperationLogRepo.countByTenantIdInAndResourceTypeAndDateRange(
              tenantIds, allTimeStart, LocalDateTime.now(), resourceType);
        } else {
          total = userOperationLogRepo.countByTenantIdIn(tenantIds);
        }

        RecentActivitiesVo vo = new RecentActivitiesVo();
        vo.setActivities(activities);
        vo.setTotal(total);
        return vo;
      }
    }.execute();
  }

  /**
   * 将 UserOperationLog 转换为 ActivityVo
   */
  private RecentActivitiesVo.ActivityVo convertToActivityVo(UserOperationLog log) {
    RecentActivitiesVo.ActivityVo vo = new RecentActivitiesVo.ActivityVo();
    vo.setId(log.getId());

    // 映射响应状态到活动类型
    vo.setType(log.getResourceType());

    // 构建标题：操作类型 + 资源类型 + 资源名称
    String actionName = getActionName(log.getAction());
    String resourceTypeName = getResourceTypeName(log.getResourceType());
    vo.setTitle(String.format("%s%s", actionName, resourceTypeName));

    // 构建描述
    vo.setDescription(log.getDetails());

    // 设置相关用户信息
    if (log.getUserId() != null) {
      vo.setRelatedUserId(log.getUserId());
    }
    vo.setRelatedUserName(log.getUserName());

    // 设置租户信息（从 TenantAuditingEntity 继承）
    if (log.getTenantId() != null) {
      vo.setRelatedTenantId(log.getTenantId());
    }

    // 格式化时间
    LocalDateTime createdDate = log.getCreatedDate();
    if (createdDate != null) {
      vo.setOccurredAt(createdDate);
    }
    return vo;
  }

  /**
   * 获取操作类型的中文名称
   */
  private String getActionName(cloud.xcan.angus.core.gm.domain.log.enums.OperationAction action) {
    if (action == null) {
      return "操作";
    }
    return switch (action) {
      case READ -> "查看";
      case CREATE -> "创建";
      case UPDATE -> "修改";
      case DELETE -> "删除";
    };
  }

  /**
   * 获取资源类型的中文名称
   */
  private String getResourceTypeName(
      cloud.xcan.angus.core.gm.domain.log.enums.ResourceType resourceType) {
    if (resourceType == null) {
      return "";
    }
    return switch (resourceType) {
      case USER -> "用户";
      case TENANT -> "租户";
      case ORGANIZATION -> "组织";
      case PERMISSION -> "权限";
      case APPLICATION -> "应用";
      case CONFIG -> "配置";
      case QUOTA -> "配额";
      case SYSTEM_EVENT -> "系统事件";
      case OTHER -> "其他";
    };
  }

  private DashboardStatisticsVo.UserStatsVo buildUserStats(List<Long> tenantIds) {
    DashboardStatisticsVo.UserStatsVo stats = new DashboardStatisticsVo.UserStatsVo();
    if (tenantIds.isEmpty()) {
      stats.setTotal(0L);
      stats.setDisabledCount(0L);
      stats.setOnlineCount(0L);
      stats.setChangeRate("0%");
      stats.setTrend(TrendEnum.FLAT);
      return stats;
    }

    long total = userRepo.countByTenantIdIn(tenantIds);
    long disabledCount = userRepo.countByTenantIdInAndStatus(tenantIds,
        UserStatus.DISABLED.getValue());
    long onlineCount = userRepo.countOnlineUsersByTenantIdIn(tenantIds);

    stats.setTotal(total);
    stats.setDisabledCount(disabledCount);
    stats.setOnlineCount(onlineCount);

    // 计算环比变化率和趋势（对比上月）
    LocalDateTime firstDayOfMonth = LocalDateTime.now()
        .with(TemporalAdjusters.firstDayOfMonth())
        .withHour(0).withMinute(0).withSecond(0).withNano(0);
    long lastMonthTotal = userRepo.countByCreatedDateBeforeAndTenantIdIn(tenantIds,
        firstDayOfMonth);
    long change = total - lastMonthTotal;
    if (lastMonthTotal > 0) {
      double changeRate = ((double) change / lastMonthTotal) * 100;
      stats.setChangeRate(String.format("%s%.1f%%", change >= 0 ? "+" : "", changeRate));
      stats.setTrend(change > 0 ? TrendEnum.UP : (change < 0 ? TrendEnum.DOWN : TrendEnum.FLAT));
    } else {
      stats.setChangeRate(change > 0 ? "+100.0%" : "0%");
      stats.setTrend(change > 0 ? TrendEnum.UP : TrendEnum.FLAT);
    }
    return stats;
  }

  private DashboardStatisticsVo.TenantStatsVo buildTenantStats() {
    TenantStatsVo tenantStatsVo = tenantQuery.getStats();

    DashboardStatisticsVo.TenantStatsVo stats = new DashboardStatisticsVo.TenantStatsVo();
    stats.setTotal(tenantStatsVo.getTotalTenants());
    stats.setEnabledCount(tenantStatsVo.getEnabledTenants());
    stats.setDisabledCount(tenantStatsVo.getDisabledTenants());

    // 计算环比变化率和趋势
    Long newTenants = tenantStatsVo.getNewTenantsThisMonth();
    if (newTenants != null && tenantStatsVo.getTotalTenants() != null) {
      long existingTenants = tenantStatsVo.getTotalTenants() - newTenants;
      if (existingTenants > 0) {
        double changeRate = ((double) newTenants / existingTenants) * 100;
        stats.setChangeRate(String.format("+%.1f%%", changeRate));
        stats.setTrend(newTenants > 0 ? TrendEnum.UP : TrendEnum.FLAT);
      } else {
        stats.setChangeRate("0%");
        stats.setTrend(TrendEnum.FLAT);
      }
    } else {
      stats.setChangeRate("0%");
      stats.setTrend(TrendEnum.FLAT);
    }
    return stats;
  }

  private DashboardStatisticsVo.OperationStatsVo buildOperationStats(List<Long> tenantIds) {
    DashboardStatisticsVo.OperationStatsVo stats = new DashboardStatisticsVo.OperationStatsVo();
    if (tenantIds.isEmpty()) {
      stats.setTotal(0L);
      stats.setErrorCount(0L);
      stats.setSuccessCount(0L);
      stats.setChangeRate("0%");
      stats.setTrend(TrendEnum.FLAT);
      return stats;
    }

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime allTimeStart = LocalDateTime.of(1970, 1, 1, 0, 0, 0);

    long total = userOperationLogRepo.countByTenantIdIn(tenantIds);
    long successCount = userOperationLogRepo.countByTenantIdInAndResponseStatusAndDateRange(
        tenantIds, allTimeStart, now, ResponseStatus.SUCCESS);
    long errorCount = userOperationLogRepo.countByTenantIdInAndResponseStatusAndDateRange(
        tenantIds, allTimeStart, now, ResponseStatus.FAILURE);

    stats.setTotal(total);
    stats.setErrorCount(errorCount);
    stats.setSuccessCount(successCount);

    LocalDateTime lastWeekStartDate = now.minusDays(7);
    LocalDateTime twoWeeksAgoStartDate = lastWeekStartDate.minusDays(7);

    long lastWeekTotal = userOperationLogRepo.countByTenantIdInAndDateRange(
        tenantIds, lastWeekStartDate, now);
    long twoWeeksAgoTotal = userOperationLogRepo.countByTenantIdInAndDateRange(
        tenantIds, twoWeeksAgoStartDate, lastWeekStartDate);

    if (twoWeeksAgoTotal > 0) {
      long change = lastWeekTotal - twoWeeksAgoTotal;
      double changeRate = ((double) change / twoWeeksAgoTotal) * 100;
      stats.setChangeRate(String.format("%s%.1f%%", change >= 0 ? "+" : "", changeRate));
      stats.setTrend(change > 0 ? TrendEnum.UP : (change < 0 ? TrendEnum.DOWN : TrendEnum.FLAT));
    } else {
      stats.setChangeRate(lastWeekTotal > 0 ? "+100.0%" : "0%");
      stats.setTrend(lastWeekTotal > 0 ? TrendEnum.UP : TrendEnum.FLAT);
    }
    return stats;
  }

  private DashboardStatisticsVo.NotificationStatsVo buildNotificationStats(List<Long> tenantIds) {
    DashboardStatisticsVo.NotificationStatsVo stats = new DashboardStatisticsVo.NotificationStatsVo();
    if (tenantIds.isEmpty()) {
      stats.setTotal(0L);
      stats.setInternalCount(0L);
      stats.setEmailCount(0L);
      stats.setSmsCount(0L);
      stats.setChangeRate("0%");
      stats.setTrend(TrendEnum.FLAT);
      return stats;
    }

    LocalDateTime now = LocalDateTime.now();

    long total = notificationRepo.countAllByTenantIdIn(tenantIds);
    long internalCount = notificationRepo.countInternalByTenantIdIn(tenantIds);
    long emailCount = notificationRepo.countEmailSentByTenantIdIn(tenantIds);
    long smsCount = smsRepo.countByTenantIdIn(tenantIds);

    stats.setTotal(total);
    stats.setInternalCount(internalCount);
    stats.setEmailCount(emailCount);
    stats.setSmsCount(smsCount);

    LocalDateTime lastWeekStartDate = now.minusDays(7);
    LocalDateTime twoWeeksAgoStartDate = lastWeekStartDate.minusDays(7);

    long lastWeekTotal = notificationRepo.countByTenantIdInAndTimestampBetween(
        tenantIds, lastWeekStartDate, now);
    long twoWeeksAgoTotal = notificationRepo.countByTenantIdInAndTimestampBetween(
        tenantIds, twoWeeksAgoStartDate, lastWeekStartDate);

    if (twoWeeksAgoTotal > 0) {
      long change = lastWeekTotal - twoWeeksAgoTotal;
      double changeRate = ((double) change / twoWeeksAgoTotal) * 100;
      stats.setChangeRate(String.format("%s%.1f%%", change >= 0 ? "+" : "", changeRate));
      stats.setTrend(change > 0 ? TrendEnum.UP : (change < 0 ? TrendEnum.DOWN : TrendEnum.FLAT));
    } else {
      stats.setChangeRate(lastWeekTotal > 0 ? "+100.0%" : "0%");
      stats.setTrend(lastWeekTotal > 0 ? TrendEnum.UP : TrendEnum.FLAT);
    }
    return stats;
  }

  private List<UserGrowthVo.GrowthDataPointVo> queryUserGrowthData(LocalDateTime startDate,
      LocalDateTime endDate, String timeRange, int dataPointCount) {
    List<UserGrowthVo.GrowthDataPointVo> dataPoints = new ArrayList<>();
    List<Long> tenantIds = tenantQuery.getTenantIdsBySameAccount();

    if (tenantIds.isEmpty()) {
      return dataPoints;
    }

    long baseCount = userRepo.countByCreatedDateBeforeAndTenantIdIn(tenantIds, startDate);

    if (timeRange.equals("7DAYS") || timeRange.equals("30DAYS")) {
      List<Object[]> dayCounts = userRepo.countUsersByDayAndTenantIdIn(tenantIds, startDate,
          endDate);
      // 将查询结果转换为 Map，key 为日期字符串（YYYY-MM-DD）
      Map<String, Long> dayCountMap = new HashMap<>();
      for (Object[] result : dayCounts) {
        java.sql.Date date = (java.sql.Date) result[0];
        Long count = ((Number) result[1]).longValue();
        dayCountMap.put(date.toString(), count);
      }

      // 遍历所有日期，计算累计用户数
      LocalDateTime current = startDate;
      long cumulativeCount = baseCount;
      while (!current.isAfter(endDate) && dataPoints.size() < dataPointCount) {
        String dateStr = current.format(DateTimeFormatter.ISO_LOCAL_DATE);
        // 如果当天有新用户，累加
        Long dayCount = dayCountMap.get(dateStr);
        if (dayCount != null) {
          cumulativeCount += dayCount;
        }

        UserGrowthVo.GrowthDataPointVo point = new UserGrowthVo.GrowthDataPointVo();
        point.setDate(dateStr);
        point.setLabel(String.format("%d月%d日", current.getMonthValue(), current.getDayOfMonth()));
        point.setUserCount(cumulativeCount);
        dataPoints.add(point);
        current = current.plusDays(1);
      }
    } else if (timeRange.equals("90DAYS")) {
      List<Object[]> weekCounts = userRepo.countUsersByWeekAndTenantIdIn(tenantIds, startDate,
          endDate);
      // 将查询结果转换为 Map，key 为 "year-week"
      Map<String, Long> weekCountMap = new HashMap<>();
      for (Object[] result : weekCounts) {
        Integer year = (Integer) result[0];
        Integer week = (Integer) result[1];
        Long count = ((Number) result[2]).longValue();
        weekCountMap.put(year + "-" + week, count);
      }

      // 遍历所有周，计算累计用户数
      LocalDateTime current = startDate;
      long cumulativeCount = baseCount;
      int weekCount = 0;
      while (!current.isAfter(endDate) && weekCount < dataPointCount) {
        LocalDateTime weekEnd = current.plusDays(7);
        // 计算当前周的年和周数
        int year = current.getYear();
        int week = current.get(WeekFields.ISO.weekOfWeekBasedYear());
        String weekKey = year + "-" + week;

        // 如果本周有新用户，累加
        Long weekCountValue = weekCountMap.get(weekKey);
        if (weekCountValue != null) {
          cumulativeCount += weekCountValue;
        }

        UserGrowthVo.GrowthDataPointVo point = new UserGrowthVo.GrowthDataPointVo();
        point.setDate(current.format(DateTimeFormatter.ISO_LOCAL_DATE));
        point.setLabel(String.format("第%d周", weekCount + 1));
        point.setUserCount(cumulativeCount);
        dataPoints.add(point);
        current = weekEnd;
        weekCount++;
      }
    } else {
      List<Object[]> monthCounts = userRepo.countUsersByMonthAndTenantIdIn(tenantIds, startDate);
      // 将查询结果转换为 Map，key 为 "year-month"
      Map<String, Long> monthCountMap = new HashMap<>();
      for (Object[] result : monthCounts) {
        Integer year = (Integer) result[0];
        Integer month = (Integer) result[1];
        Long count = ((Number) result[2]).longValue();
        monthCountMap.put(year + "-" + month, count);
      }

      // 遍历所有月份，计算累计用户数
      LocalDateTime current = startDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
          .withNano(0);
      long cumulativeCount = baseCount;
      int monthCount = 0;
      while (!current.isAfter(endDate) && monthCount < dataPointCount) {
        LocalDateTime monthEnd = current.plusMonths(1);
        String monthKey = current.getYear() + "-" + current.getMonthValue();

        // 如果本月有新用户，累加
        Long monthCountValue = monthCountMap.get(monthKey);
        if (monthCountValue != null) {
          cumulativeCount += monthCountValue;
        }

        UserGrowthVo.GrowthDataPointVo point = new UserGrowthVo.GrowthDataPointVo();
        point.setDate(current.format(DateTimeFormatter.ISO_LOCAL_DATE));
        point.setLabel(String.format("%d月", current.getMonthValue()));
        point.setUserCount(cumulativeCount);
        dataPoints.add(point);
        current = monthEnd;
        monthCount++;
      }
    }
    return dataPoints;
  }
}
