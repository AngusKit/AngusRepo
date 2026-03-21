package cloud.xcan.angus.core.gm.infra.job;

import static cloud.xcan.angus.api.commonlink.setting.Setting.getDefaultAlertRuleSettings;
import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.formatFileSize;
import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.formatPercent;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.alert.AlertLevel;
import cloud.xcan.angus.api.commonlink.setting.alert.AlertRuleSettings;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.core.gm.application.cmd.notification.NotificationHelperCmd;
import cloud.xcan.angus.core.gm.application.cmd.system.AlertRecordCmd;
import cloud.xcan.angus.core.gm.application.query.security.SecurityQuery;
import cloud.xcan.angus.core.gm.application.query.system.SystemMonitoringQuery;
import cloud.xcan.angus.core.gm.domain.notification.NotificationMessage;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationPriority;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationType;
import cloud.xcan.angus.core.gm.domain.security.model.RecipientUser;
import cloud.xcan.angus.core.gm.domain.security.model.SecurityNotificationConfig;
import cloud.xcan.angus.core.gm.domain.system.AlertRecord;
import cloud.xcan.angus.core.gm.domain.system.enums.AlertRecordStatus;
import cloud.xcan.angus.core.gm.infra.monitoring.TimeSeriesDataService;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.HealthCheckVo;
import cloud.xcan.angus.core.job.JobTemplate;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

/**
 * 监控数据采集任务
 * <p>
 * 定期采集系统监控数据（CPU、内存、网络）并写入时序数据服务
 * </p>
 */
@Slf4j
@Component
public class SystemMonitoringJob {

  private static final String LOCK_KEY = "gm:job:SystemMonitoringJob";

  /**
   * 系统负载告警阈值（使用率超过此值时触发通知），默认85%
   */
  private static final double SYSTEM_LOAD_ALERT_THRESHOLD = 85.0;

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private TimeSeriesDataService timeSeriesDataService;

  @Resource
  private SystemMonitoringQuery systemMonitoringService;

  @Resource
  private SettingManager settingManager;

  @Resource
  private AlertRecordCmd alertRecordCmd;

  @Resource
  private ApplicationInfo applicationInfo;

  @Resource
  private NotificationHelperCmd notificationHelperCmd;

  @Resource
  private SecurityQuery securityQuery;

  // OSHI 组件（用于直接获取原始数据）
  private SystemInfo systemInfo;
  private CentralProcessor processor;
  private GlobalMemory memory;
  private OperatingSystem os;

  // 用于计算CPU使用率的上一次采样值
  private long[] previousCpuTicks;

  // 用于计算网络流量速率的上一次采样值
  private final AtomicLong previousBytesIn = new AtomicLong(0);
  private final AtomicLong previousBytesOut = new AtomicLong(0);
  private LocalDateTime previousNetworkSampleTime;

  // 用于记录上次系统负载通知时间（限制每小时最多通知一次）
  private LocalDateTime lastCpuNotificationTime;
  private LocalDateTime lastMemoryNotificationTime;
  private LocalDateTime lastDiskNotificationTime;
  private LocalDateTime lastComponentNotificationTime;

  @PostConstruct
  public void init() {
    systemInfo = new SystemInfo();
    processor = systemInfo.getHardware().getProcessor();
    memory = systemInfo.getHardware().getMemory();
    os = systemInfo.getOperatingSystem();
    previousNetworkSampleTime = LocalDateTime.now();

    // 初始化CPU tick计数器
    previousCpuTicks = processor.getSystemCpuLoadTicks();

    // 初始化网络流量基准值
    List<NetworkIF> networkIFs = systemInfo.getHardware().getNetworkIFs();
    long totalBytesIn = 0;
    long totalBytesOut = 0;
    for (NetworkIF netIF : networkIFs) {
      netIF.updateAttributes();
      totalBytesIn += netIF.getBytesRecv();
      totalBytesOut += netIF.getBytesSent();
    }
    previousBytesIn.set(totalBytesIn);
    previousBytesOut.set(totalBytesOut);

    log.info("Monitoring data collection job initialization completed");
  }

  /**
   * 执行监控数据采集 每15秒执行一次
   */
  @Scheduled(fixedDelay = 15 * 1000, initialDelay = 30000)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 2, TimeUnit.MINUTES, () -> {
      try {
        LocalDateTime now = LocalDateTime.now();

        // 采集CPU使用率
        collectCpuData(now);

        // 采集内存使用数据
        collectMemoryData(now);

        // 采集网络流量数据
        collectNetworkData(now);

        // 检查告警并发送通知
        checkAndSendAlerts(now);

        log.debug("监控数据采集完成：{}", now);
      } catch (Exception e) {
        log.error("监控数据采集失败", e);
      }
    });
  }

  /**
   * 采集CPU使用率数据
   */
  private void collectCpuData(LocalDateTime time) {
    try {
      // 通过两次采样计算CPU使用率
      double cpuUsage = getCpuUsage();
      timeSeriesDataService.addCpuData(time, formatPercent(cpuUsage));
    } catch (Exception e) {
      log.error("采集CPU数据失败", e);
    }
  }

  /**
   * 采集内存使用数据
   */
  private void collectMemoryData(LocalDateTime time) {
    try {
      long totalMemory = memory.getTotal();
      long availableMemory = memory.getAvailable();
      long usedMemory = totalMemory - availableMemory;
      double usagePercent = totalMemory > 0 ? (double) usedMemory / totalMemory * 100 : 0;

      String used = formatFileSize(usedMemory);
      timeSeriesDataService.addMemoryData(time, used, formatPercent(usagePercent));
    } catch (Exception e) {
      log.error("采集内存数据失败", e);
    }
  }

  /**
   * 采集网络流量数据
   */
  private void collectNetworkData(LocalDateTime time) {
    try {
      List<NetworkIF> networkIFs = systemInfo.getHardware().getNetworkIFs();
      long totalBytesIn = 0;
      long totalBytesOut = 0;

      for (NetworkIF netIF : networkIFs) {
        netIF.updateAttributes();
        totalBytesIn += netIF.getBytesRecv();
        totalBytesOut += netIF.getBytesSent();
      }

      // 计算速率（字节/秒）
      long previousIn = previousBytesIn.get();
      long previousOut = previousBytesOut.get();
      LocalDateTime previousTime = previousNetworkSampleTime;

      long inRate = 0;
      long outRate = 0;

      if (previousTime != null && previousIn > 0 && previousOut > 0) {
        // 计算时间差（秒）
        long seconds = java.time.Duration.between(previousTime, time).getSeconds();
        if (seconds > 0) {
          inRate = (totalBytesIn - previousIn) / seconds;
          outRate = (totalBytesOut - previousOut) / seconds;
        }
      }

      // 更新上一次采样值
      previousBytesIn.set(totalBytesIn);
      previousBytesOut.set(totalBytesOut);
      previousNetworkSampleTime = time;

      String inRateStr = formatFileSize(Math.max(0, inRate)) + "/s";
      String outRateStr = formatFileSize(Math.max(0, outRate)) + "/s";

      timeSeriesDataService.addNetworkData(time, inRateStr, outRateStr);
    } catch (Exception e) {
      log.error("采集网络数据失败", e);
    }
  }

  /**
   * 获取CPU使用率（通过两次采样计算）
   */
  private double getCpuUsage() {
    long[] currentTicks = processor.getSystemCpuLoadTicks();
    long[] prevTicks = previousCpuTicks;

    // 如果还没有上一次的采样值，先保存当前值并返回0
    if (prevTicks == null) {
      previousCpuTicks = currentTicks;
      return 0.0;
    }

    // 更新上一次采样值
    previousCpuTicks = currentTicks;

    // 计算CPU使用率：100 * (1 - (idleTime / totalTime))
    // TickType 索引：USER=0, NICE=1, SYSTEM=2, IDLE=3, IOWAIT=4, IRQ=5, SOFTIRQ=6
    long totalTicks = 0;
    long idleTicks = 0;

    // IDLE tick 索引为 3
    int idleIndex = 3;
    if (currentTicks.length > idleIndex && prevTicks.length > idleIndex) {
      // 计算总tick数（所有类型的tick差值之和）
      for (int i = 0; i < currentTicks.length && i < prevTicks.length; i++) {
        totalTicks += currentTicks[i] - prevTicks[i];
      }
      // idle ticks
      idleTicks = currentTicks[idleIndex] - prevTicks[idleIndex];
    }

    if (totalTicks > 0) {
      return 100.0 * (1.0 - (double) idleTicks / totalTicks);
    }
    return 0.0;
  }

  /**
   * 检查告警并保存告警记录
   */
  private void checkAndSendAlerts(LocalDateTime time) {
    try {
      List<AlertRecord> alertRecords = new ArrayList<>();

      // 1. 根据检查监控数据和高级规则触发告警记录
      List<AlertRecord> ruleAlerts = checkAlertRules(time);
      alertRecords.addAll(ruleAlerts);

      // 2. 检查系统负载（CPU、内存、磁盘）使用率是否超过85%
      checkSystemLoadAndNotify(time);

      // 3. 根据systemMonitoringService.getHealth()结果告警异常状态组件
      List<AlertRecord> healthAlerts = checkHealthComponents(time);
      alertRecords.addAll(healthAlerts);

      // 4. 检查系统服务组件状态
      checkServiceComponentAndNotify(healthAlerts);

      // 5. 保存告警记录到数据库
      if (!alertRecords.isEmpty()) {
        for (AlertRecord record : alertRecords) {
          try {
            alertRecordCmd.create(record);
          } catch (Exception e) {
            log.error("保存告警记录失败：{}", record.getRuleName(), e);
          }
        }
        log.info("已保存 {} 条告警记录", alertRecords.size());
      }

    } catch (Exception e) {
      log.error("检查告警并保存告警记录失败", e);
    }
  }

  /**
   * 检查系统负载并发送通知（限制每小时最多通知一次） 当 systemLoadHighNotify 开启时，根据 recipientUsers 配置中的用户ID触发告警通知
   */
  private void checkSystemLoadAndNotify(LocalDateTime time) {
    try {
      // 获取安全通知配置，检查 systemLoadHighNotify 是否开启
      var security = securityQuery.getNotificationConfig();
      if (security == null
          || !(security.getConfig() instanceof SecurityNotificationConfig config)) {
        return;
      }
      if (!Boolean.TRUE.equals(config.getSystemLoadHighNotify())) {
        return;
      }
      // 从 recipientUsers 获取通知接收用户ID列表
      List<RecipientUser> recipientUsers = config.getRecipientUsers();
      if (recipientUsers == null || recipientUsers.isEmpty()) {
        return;
      }
      List<Long> userIds = recipientUsers.stream()
          .map(RecipientUser::getId)
          .filter(Objects::nonNull)
          .toList();
      if (userIds.isEmpty()) {
        return;
      }

      // 获取当前监控数据
      double cpuUsage = getCpuUsage();
      long totalMemory = memory.getTotal();
      long availableMemory = memory.getAvailable();
      double memoryUsage =
          totalMemory > 0 ? (double) (totalMemory - availableMemory) / totalMemory * 100 : 0;

      // 获取磁盘使用率
      double diskUsage = 0.0;
      oshi.software.os.FileSystem fileSystem = os.getFileSystem();
      List<oshi.software.os.OSFileStore> fileStores = fileSystem.getFileStores();
      if (!fileStores.isEmpty()) {
        oshi.software.os.OSFileStore fileStore = fileStores.get(0);
        long totalSpace = fileStore.getTotalSpace();
        long freeSpace = fileStore.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;
        diskUsage = totalSpace > 0 ? (double) usedSpace / totalSpace * 100 : 0;
      }

      // 检查CPU使用率是否超过85%
      if (cpuUsage > SYSTEM_LOAD_ALERT_THRESHOLD) {
        // 检查距离上次通知是否已超过1小时
        if (lastCpuNotificationTime == null
            || java.time.Duration.between(lastCpuNotificationTime, time).toHours() >= 1) {
          String resourceType = "CPU";
          String usageStr = String.format("%.2f", cpuUsage);
          notificationHelperCmd.createBatchByMessageKey(
              NotificationType.WARNING,
              NotificationMessage.SYSTEM_LOAD_HIGH_TITLE,
              NotificationMessage.SYSTEM_LOAD_HIGH_DESCRIPTION,
              NotificationMessage.CATEGORY_SYSTEM_MONITORING,
              NotificationPriority.HIGH,
              userIds,
              new Object[]{resourceType},
              new Object[]{resourceType, usageStr}
          );
          lastCpuNotificationTime = time;
          log.warn("系统CPU使用率超过85%：{}%，已通知配置的接收用户", cpuUsage);
        } else {
          log.debug("系统CPU使用率超过85%：{}%，但距离上次通知不足1小时，跳过通知", cpuUsage);
        }
      }

      // 检查内存使用率是否超过85%
      if (memoryUsage > SYSTEM_LOAD_ALERT_THRESHOLD) {
        // 检查距离上次通知是否已超过1小时
        if (lastMemoryNotificationTime == null
            || java.time.Duration.between(lastMemoryNotificationTime, time).toHours() >= 1) {
          String resourceType = "内存";
          String usageStr = String.format("%.2f", memoryUsage);
          notificationHelperCmd.createBatchByMessageKey(
              NotificationType.WARNING,
              NotificationMessage.SYSTEM_LOAD_HIGH_TITLE,
              NotificationMessage.SYSTEM_LOAD_HIGH_DESCRIPTION,
              NotificationMessage.CATEGORY_SYSTEM_MONITORING,
              NotificationPriority.HIGH,
              userIds,
              new Object[]{resourceType},
              new Object[]{resourceType, usageStr}
          );
          lastMemoryNotificationTime = time;
          log.warn("系统内存使用率超过85%：{}%，已通知配置的接收用户", memoryUsage);
        } else {
          log.debug("系统内存使用率超过85%：{}%，但距离上次通知不足1小时，跳过通知", memoryUsage);
        }
      }

      // 检查磁盘使用率是否超过85%
      if (diskUsage > SYSTEM_LOAD_ALERT_THRESHOLD) {
        // 检查距离上次通知是否已超过1小时
        if (lastDiskNotificationTime == null
            || java.time.Duration.between(lastDiskNotificationTime, time).toHours() >= 1) {
          String resourceType = "磁盘";
          String usageStr = String.format("%.2f", diskUsage);
          notificationHelperCmd.createBatchByMessageKey(
              NotificationType.WARNING,
              NotificationMessage.SYSTEM_LOAD_HIGH_TITLE,
              NotificationMessage.SYSTEM_LOAD_HIGH_DESCRIPTION,
              NotificationMessage.CATEGORY_SYSTEM_MONITORING,
              NotificationPriority.HIGH,
              userIds,
              new Object[]{resourceType},
              new Object[]{resourceType, usageStr}
          );
          lastDiskNotificationTime = time;
          log.warn("系统磁盘使用率超过85%：{}%，已通知配置的接收用户", diskUsage);
        } else {
          log.debug("系统磁盘使用率超过85%：{}%，但距离上次通知不足1小时，跳过通知", diskUsage);
        }
      }
    } catch (Exception e) {
      log.error("检查系统负载并发送通知失败", e);
    }
  }

  /**
   * 检查服务组件异常并发送通知（限制每小时最多通知一次） 当 serviceComponentAbnormalNotify 开启时，根据 recipientUsers
   * 配置中的用户ID触发告警通知
   */
  private void checkServiceComponentAndNotify(List<AlertRecord> healthAlerts) {
    try {
      if (healthAlerts == null || healthAlerts.isEmpty()) {
        return;
      }
      var security = securityQuery.getNotificationConfig();
      if (security == null
          || !(security.getConfig() instanceof SecurityNotificationConfig config)) {
        return;
      }
      if (!Boolean.TRUE.equals(config.getServiceComponentAbnormalNotify())) {
        return;
      }
      List<RecipientUser> recipientUsers = config.getRecipientUsers();
      if (recipientUsers == null || recipientUsers.isEmpty()) {
        return;
      }
      List<Long> userIds = recipientUsers.stream()
          .map(RecipientUser::getId)
          .filter(Objects::nonNull)
          .toList();
      if (userIds.isEmpty()) {
        return;
      }
      LocalDateTime now = LocalDateTime.now();
      if (lastComponentNotificationTime != null
          && java.time.Duration.between(lastComponentNotificationTime, now).toHours() < 1) {
        log.debug("服务组件异常，但距离上次通知不足1小时，跳过通知");
        return;
      }
      String componentDetails = healthAlerts.stream()
          .map(r -> r.getComponentName() != null
              ? String.format("%s：%s", r.getComponentName(), r.getComponentStatus())
              : r.getDescription())
          .reduce((a, b) -> a + "；" + b)
          .orElse("");
      notificationHelperCmd.createBatchByMessageKey(
          NotificationType.WARNING,
          NotificationMessage.SERVICE_COMPONENT_ABNORMAL_TITLE,
          NotificationMessage.SERVICE_COMPONENT_ABNORMAL_DESCRIPTION,
          NotificationMessage.CATEGORY_SYSTEM_MONITORING,
          NotificationPriority.HIGH,
          userIds,
          null,
          new Object[]{componentDetails}
      );
      lastComponentNotificationTime = now;
      log.warn("服务组件状态异常，已通知配置的接收用户：{}", componentDetails);
    } catch (Exception e) {
      log.error("检查服务组件并发送通知失败", e);
    }
  }

  /**
   * 检查告警规则
   */
  private List<AlertRecord> checkAlertRules(LocalDateTime triggerTime) {
    List<AlertRecord> alertRecords = new ArrayList<>();
    try {
      // 获取告警规则设置
      Setting settings = settingManager.getSetting0(SettingKey.ALERT_RULES);
      AlertRuleSettings alertRules = nullSafe(settings != null ? settings.getAlertRules() : null,
          getDefaultAlertRuleSettings());

      // 获取当前监控数据
      double cpuUsage = getCpuUsage();
      long totalMemory = memory.getTotal();
      long availableMemory = memory.getAvailable();
      double memoryUsage =
          totalMemory > 0 ? (double) (totalMemory - availableMemory) / totalMemory * 100 : 0;

      // 获取磁盘使用率
      double diskUsage = 0.0;
      oshi.software.os.FileSystem fileSystem = os.getFileSystem();
      List<oshi.software.os.OSFileStore> fileStores = fileSystem.getFileStores();
      if (!fileStores.isEmpty()) {
        oshi.software.os.OSFileStore fileStore = fileStores.get(0);
        long totalSpace = fileStore.getTotalSpace();
        long freeSpace = fileStore.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;
        diskUsage = totalSpace > 0 ? (double) usedSpace / totalSpace * 100 : 0;
      }

      // 检查每个告警规则
      for (var rule : alertRules.getRules()) {
        String metric = rule.getMetric();
        String condition = rule.getCondition();
        Double threshold = rule.getThreshold();

        if (threshold == null || condition == null || metric == null) {
          continue;
        }

        double currentValue = 0.0;
        String metricName = "";

        switch (metric) {
          case "cpu_usage":
            currentValue = cpuUsage;
            metricName = "CPU使用率";
            break;
          case "memory_usage":
            currentValue = memoryUsage;
            metricName = "内存使用率";
            break;
          case "disk_usage":
            currentValue = diskUsage;
            metricName = "磁盘使用率";
            break;
          default:
            continue;
        }

        // 检查是否触发告警
        boolean triggered = false;
        switch (condition) {
          case ">":
            triggered = currentValue > threshold;
            break;
          case ">=":
            triggered = currentValue >= threshold;
            break;
          case "<":
            triggered = currentValue < threshold;
            break;
          case "<=":
            triggered = currentValue <= threshold;
            break;
          case "==":
            triggered = Math.abs(currentValue - threshold) < 0.01;
            break;
          default:
            continue;
        }

        if (triggered) {
          // 创建告警记录
          AlertRecord record = new AlertRecord();
          record.setRuleName(rule.getName());
          record.setMetric(metric);
          record.setMetricName(metricName);
          record.setCurrentValue(currentValue);
          record.setThreshold(threshold);
          record.setCondition(condition);
          record.setLevel(rule.getLevel());
          record.setStatus(AlertRecordStatus.ACTIVE);
          record.setTriggerTime(triggerTime);
          record.setDescription(String.format("%s当前值为 %.2f%%，超过阈值 %.2f%%（条件：%s）",
              metricName, currentValue, threshold, condition));
          record.setInstanceId(applicationInfo.getInstanceId());
          alertRecords.add(record);
        }
      }
    } catch (Exception e) {
      log.error("检查告警规则失败", e);
    }
    return alertRecords;
  }

  /**
   * 检查健康检查结果中的异常组件
   */
  private List<AlertRecord> checkHealthComponents(LocalDateTime triggerTime) {
    List<AlertRecord> alertRecords = new ArrayList<>();
    try {
      HealthCheckVo health = systemMonitoringService.getHealth();
      if (health == null || health.getComponents() == null) {
        return alertRecords;
      }

      // 检查整体状态
      if (!"UP".equalsIgnoreCase(health.getStatus())) {
        AlertRecord record = new AlertRecord();
        record.setRuleName("系统健康检查");
        record.setMetric("system_health");
        record.setMetricName("系统健康状态");
        record.setCurrentValue(0.0);
        record.setThreshold(0.0);
        record.setCondition("==");
        record.setLevel(AlertLevel.HIGH);
        record.setStatus(AlertRecordStatus.ACTIVE);
        record.setTriggerTime(triggerTime);
        record.setDescription(String.format("系统健康状态异常：%s", health.getStatus()));
        record.setInstanceId(applicationInfo.getInstanceId());
        alertRecords.add(record);
      }

      // 检查各个组件的状态
      Map<String, HealthCheckVo.ComponentHealth> components = health.getComponents();
      if (components != null) {
        for (Map.Entry<String, HealthCheckVo.ComponentHealth> entry : components.entrySet()) {
          String componentName = entry.getKey();
          HealthCheckVo.ComponentHealth component = entry.getValue();

          if (component != null && !"UP".equalsIgnoreCase(component.getStatus())) {
            AlertRecord record = new AlertRecord();
            record.setRuleName("组件健康检查");
            record.setMetric("component_health");
            record.setMetricName("组件健康状态");
            record.setCurrentValue(0.0);
            record.setThreshold(0.0);
            record.setCondition("==");
            record.setLevel(AlertLevel.HIGH);
            record.setStatus(AlertRecordStatus.ACTIVE);
            record.setTriggerTime(triggerTime);
            record.setComponentName(componentName);
            record.setComponentStatus(component.getStatus());
            record.setDescription(
                String.format("组件「%s」状态异常：%s", componentName, component.getStatus()));
            record.setInstanceId(applicationInfo.getInstanceId());
            alertRecords.add(record);
          }
        }
      }
    } catch (Exception e) {
      log.error("检查健康检查结果失败", e);
    }
    return alertRecords;
  }
}
