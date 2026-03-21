package cloud.xcan.angus.core.gm.application.query.system.impl;

import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.formatFileSize;
import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.formatPercent;
import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.formatUptime;
import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.parsePeriodHour;

import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.application.query.system.SystemMonitoringQuery;
import cloud.xcan.angus.core.gm.infra.monitoring.TimeSeriesDataService;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.CpuUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.CpuUsageVo.CpuHistory;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.DiskUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.DiskUsageVo.DiskInfo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.EnvironmentVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.HealthCheckVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.HealthCheckVo.ComponentHealth;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.MemoryUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.MemoryUsageVo.MemoryHistory;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.MonitoringOverviewVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.NetworkUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.NetworkUsageVo.NetworkHistory;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.NetworkUsageVo.NetworkInterface;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.ProcessInfoVo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.NetworkIF;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

/**
 * 系统监控服务实现（基于OSHI和Spring Boot Actuator）
 */
@Service
public class SystemMonitoringQueryImpl implements SystemMonitoringQuery {

  private SystemInfo systemInfo;
  private CentralProcessor processor;
  private GlobalMemory memory;
  private OperatingSystem os;
  private long[] previousCpuTicks;
  private Map<String, long[]> previousNetworkBytesByInterface = new HashMap<>();
  private LocalDateTime previousNetworkSampleTime;
  private long previousOverviewBytesIn = -1;
  private long previousOverviewBytesOut = -1;
  private long previousOverviewNetworkTime = -1;

  @Resource
  private TimeSeriesDataService timeSeriesDataService;

  @Resource
  private HealthEndpoint healthEndpoint;

  @Resource
  private ApplicationQuery applicationQuery;

  @PostConstruct
  public void init() {
    systemInfo = new SystemInfo();
    processor = systemInfo.getHardware().getProcessor();
    memory = systemInfo.getHardware().getMemory();
    os = systemInfo.getOperatingSystem();
    // 初始化CPU tick计数器
    previousCpuTicks = processor.getSystemCpuLoadTicks();
  }


  @Override
  public EnvironmentVo getEnvironment() {
    EnvironmentVo vo = new EnvironmentVo();
    SystemInfo systemInfo = new SystemInfo();
    OperatingSystem os = systemInfo.getOperatingSystem();
    CentralProcessor processor = systemInfo.getHardware().getProcessor();
    GlobalMemory memory = systemInfo.getHardware().getMemory();

    // 操作系统信息
    EnvironmentVo.OsInfo osInfo = new EnvironmentVo.OsInfo();
    osInfo.setName(os.getFamily());
    osInfo.setVersion(os.getVersionInfo().getVersion());
    osInfo.setArch(System.getProperty("os.arch"));
    vo.setOs(osInfo);

    // Java信息
    EnvironmentVo.JavaInfo javaInfo = new EnvironmentVo.JavaInfo();
    javaInfo.setVersion(System.getProperty("java.version"));
    javaInfo.setVendor(System.getProperty("java.vendor"));
    javaInfo.setHome(System.getProperty("java.home"));
    vo.setJava(javaInfo);

    // 内存信息
    EnvironmentVo.MemoryInfo memoryInfo = new EnvironmentVo.MemoryInfo();
    long totalMemory = memory.getTotal();
    long availableMemory = memory.getAvailable();
    long usedMemory = totalMemory - availableMemory;
    long maxMemory = Runtime.getRuntime().maxMemory();
    memoryInfo.setTotal(formatFileSize(totalMemory));
    memoryInfo.setMax(formatFileSize(maxMemory));
    memoryInfo.setFree(formatFileSize(availableMemory));
    memoryInfo.setUsed(formatFileSize(usedMemory));
    vo.setMemory(memoryInfo);

    // CPU信息
    EnvironmentVo.CpuInfo cpuInfo = new EnvironmentVo.CpuInfo();
    cpuInfo.setCores(processor.getPhysicalProcessorCount());
    cpuInfo.setProcessors(processor.getLogicalProcessorCount());
    cpuInfo.setModel(processor.getProcessorIdentifier().getName());
    vo.setCpu(cpuInfo);

    // 磁盘信息（取第一个文件系统）
    EnvironmentVo.DiskInfo diskInfo = new EnvironmentVo.DiskInfo();
    List<OSFileStore> fileStores = os.getFileSystem().getFileStores();
    if (!fileStores.isEmpty()) {
      OSFileStore fileStore = fileStores.get(0);
      long totalSpace = fileStore.getTotalSpace();
      long freeSpace = fileStore.getFreeSpace();
      long usableSpace = fileStore.getUsableSpace();
      diskInfo.setTotal(formatFileSize(totalSpace));
      diskInfo.setFree(formatFileSize(freeSpace));
      diskInfo.setUsable(formatFileSize(usableSpace));
    }
    vo.setDisk(diskInfo);

    // 获取系统IP地址（优先IPv4，如果没有则使用IPv6）
    String systemIp = getSystemIp();
    vo.setIp(systemIp);

    return vo;
  }

  @Override
  public MonitoringOverviewVo getOverview() {
    MonitoringOverviewVo vo = new MonitoringOverviewVo();

    // CPU使用率（通过两次采样计算）
    // 如果previousCpuTicks为null或与当前值相同，先进行一次采样并等待
    if (previousCpuTicks == null) {
      previousCpuTicks = processor.getSystemCpuLoadTicks();
      try {
        Thread.sleep(100); // 等待100ms以确保有足够的时间间隔
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    double cpuUsage = getCpuUsage();
    vo.setCpuUsage(formatPercent(cpuUsage));

    // 内存使用率
    long totalMemory = memory.getTotal();
    long availableMemory = memory.getAvailable();
    long usedMemory = totalMemory - availableMemory;
    double memoryUsage = (double) usedMemory / totalMemory * 100;
    vo.setMemoryUsage(formatPercent(memoryUsage));

    // 磁盘使用率（取第一个文件系统）
    oshi.software.os.FileSystem fileSystem = os.getFileSystem();
    List<oshi.software.os.OSFileStore> fileStores = fileSystem.getFileStores();
    if (!fileStores.isEmpty()) {
      oshi.software.os.OSFileStore fileStore = fileStores.get(0);
      long totalSpace = fileStore.getTotalSpace();
      long freeSpace = fileStore.getFreeSpace();
      long usedSpace = totalSpace - freeSpace;
      double diskUsage = totalSpace > 0 ? (double) usedSpace / totalSpace * 100 : 0;
      vo.setDiskUsage(formatPercent(diskUsage));
    }

    // 网络流量（速率，单位 B/s、KB/s、MB/s、GB/s）
    List<NetworkIF> networkIFs = systemInfo.getHardware().getNetworkIFs();
    long bytesIn = 0;
    long bytesOut = 0;
    for (NetworkIF netIF : networkIFs) {
      netIF.updateAttributes();
      bytesIn += netIF.getBytesRecv();
      bytesOut += netIF.getBytesSent();
    }
    long now = System.currentTimeMillis();
    if (previousOverviewBytesIn >= 0 && previousOverviewBytesOut >= 0
        && previousOverviewNetworkTime > 0) {
      long elapsedMs = Math.max(1, now - previousOverviewNetworkTime);
      long inRateBps = Math.max(0, (bytesIn - previousOverviewBytesIn) * 1000L / elapsedMs);
      long outRateBps = Math.max(0, (bytesOut - previousOverviewBytesOut) * 1000L / elapsedMs);
      vo.setNetworkIn(formatFileSize(inRateBps) + "/s");
      vo.setNetworkOut(formatFileSize(outRateBps) + "/s");
    } else {
      vo.setNetworkIn("0 B/s");
      vo.setNetworkOut("0 B/s");
    }
    previousOverviewBytesIn = bytesIn;
    previousOverviewBytesOut = bytesOut;
    previousOverviewNetworkTime = now;

    // 系统状态
    vo.setSystemStatus(cpuUsage < 80 && memoryUsage < 80 ? "NORMAL" : "WARNING");

    // 运行时间
    long uptimeSeconds = os.getSystemUptime();
    vo.setUptime(formatUptime(uptimeSeconds));

    vo.setLastUpdateTime(LocalDateTime.now());
    return vo;
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
        long diff = currentTicks[i] - prevTicks[i];
        // 防止负数（tick值可能回绕）
        if (diff >= 0) {
          totalTicks += diff;
        }
      }
      // idle ticks
      long idleDiff = currentTicks[idleIndex] - prevTicks[idleIndex];
      if (idleDiff >= 0) {
        idleTicks = idleDiff;
      }
    }

    if (totalTicks > 0) {
      return 100.0 * (1.0 - (double) idleTicks / totalTicks);
    }
    // 如果totalTicks为0，说明两次采样时间间隔太短或tick值没有变化，返回0
    // 这种情况通常发生在第一次调用或时间间隔太短时
    return 0.0;
  }

  @Override
  public HealthCheckVo getHealth() {
    HealthCheckVo vo = new HealthCheckVo();
    Map<String, ComponentHealth> components = new HashMap<>();

    // 从 Spring Boot Actuator 获取健康检查信息
    HealthComponent healthComponent = healthEndpoint.health();

    // 设置整体状态
    vo.setStatus(healthComponent.getStatus().getCode());

    // 转换各个组件的健康检查信息
    if (healthComponent instanceof Health) {
      Health health = (Health) healthComponent;
      Map<String, Object> healthDetails = health.getDetails();
      if (healthDetails != null) {
        for (Map.Entry<String, Object> entry : healthDetails.entrySet()) {
          String componentName = entry.getKey();
          Object componentValue = entry.getValue();

          ComponentHealth componentHealth = convertToComponentHealth(componentValue);
          if (componentHealth != null) {
            components.put(componentName, componentHealth);
          }
        }
      }
    }

    vo.setComponents(components);
    return vo;
  }

  /**
   * 将 Actuator HealthComponent 转换为 ComponentHealth
   */
  private ComponentHealth convertToComponentHealth(Object healthComponent) {
    if (healthComponent == null) {
      return null;
    }

    ComponentHealth componentHealth = new ComponentHealth();

    if (healthComponent instanceof Health) {
      Health health = (Health) healthComponent;
      componentHealth.setStatus(health.getStatus().getCode());

      // 提取 details
      Map<String, Object> details = health.getDetails();
      if (details != null && !details.isEmpty()) {
        // 移除 status 字段（已在 ComponentHealth.status 中）
        Map<String, Object> filteredDetails = new HashMap<>(details);
        filteredDetails.remove("status");
        componentHealth.setDetails(filteredDetails);
      }

      // 尝试从 details 中提取 responseTime（如果存在）
      if (details != null && details.containsKey("responseTime")) {
        Object responseTime = details.get("responseTime");
        if (responseTime instanceof Number) {
          componentHealth.setResponseTime(((Number) responseTime).intValue());
        }
      }
    } else if (healthComponent instanceof HealthComponent) {
      HealthComponent component = (HealthComponent) healthComponent;
      componentHealth.setStatus(component.getStatus().getCode());
    } else if (healthComponent instanceof Map) {
      // 处理 Map 类型的数据
      @SuppressWarnings("unchecked")
      Map<String, Object> componentMap = (Map<String, Object>) healthComponent;

      // 提取 status
      Object statusObj = componentMap.get("status");
      if (statusObj instanceof String) {
        componentHealth.setStatus((String) statusObj);
      } else if (statusObj instanceof Map) {
        @SuppressWarnings("unchecked")
        Map<String, Object> statusMap = (Map<String, Object>) statusObj;
        Object code = statusMap.get("code");
        if (code instanceof String) {
          componentHealth.setStatus((String) code);
        }
      }

      // 提取 details（排除 status）
      Map<String, Object> details = new HashMap<>(componentMap);
      details.remove("status");
      componentHealth.setDetails(details);

      // 提取 responseTime
      if (componentMap.containsKey("responseTime")) {
        Object responseTime = componentMap.get("responseTime");
        if (responseTime instanceof Number) {
          componentHealth.setResponseTime(((Number) responseTime).intValue());
        }
      }
    } else {
      // 其他类型，尝试转换为字符串状态
      componentHealth.setStatus("UNKNOWN");
      Map<String, Object> details = new HashMap<>();
      details.put("value", healthComponent.toString());
      componentHealth.setDetails(details);
    }

    return componentHealth;
  }

  @Override
  public CpuUsageVo getCpuUsage(String period) {
    CpuUsageVo vo = new CpuUsageVo();

    // 当前CPU使用率（通过两次采样计算）
    double currentUsage = getCpuUsage();
    vo.setCurrent(formatPercent(currentUsage));

    // CPU核心数
    vo.setCores(processor.getLogicalProcessorCount());

    // 从时序数据服务获取历史数据
    List<CpuHistory> history = timeSeriesDataService.getCpuHistory(parsePeriodHour(period));
    vo.setHistory(history);

    // 计算平均值、最大值、最小值
    if (!history.isEmpty()) {
      double sum = history.stream().mapToDouble(CpuHistory::getUsage).sum();
      vo.setAverage(formatPercent(sum / history.size()));
      vo.setMax(formatPercent(history.stream().mapToDouble(CpuHistory::getUsage).max().orElse(0)));
      vo.setMin(formatPercent(history.stream().mapToDouble(CpuHistory::getUsage).min().orElse(0)));
    } else {
      // 如果没有历史数据，使用当前值
      vo.setAverage(formatPercent(currentUsage));
      vo.setMax(formatPercent(currentUsage));
      vo.setMin(formatPercent(currentUsage));
    }

    return vo;
  }

  @Override
  public MemoryUsageVo getMemoryUsage(String period) {
    MemoryUsageVo vo = new MemoryUsageVo();

    long totalMemory = memory.getTotal();
    long availableMemory = memory.getAvailable();
    long usedMemory = totalMemory - availableMemory;
    double usagePercent = (double) usedMemory / totalMemory * 100;

    vo.setTotal(formatFileSize(totalMemory));
    vo.setUsed(formatFileSize(usedMemory));
    vo.setFree(formatFileSize(availableMemory));
    vo.setUsagePercent(formatPercent(usagePercent));

    // 交换空间
    long swapTotal = memory.getVirtualMemory().getSwapTotal();
    long swapUsed = memory.getVirtualMemory().getSwapUsed();
    long swapFree = swapTotal - swapUsed;
    vo.setSwapTotal(formatFileSize(swapTotal));
    vo.setSwapUsed(formatFileSize(swapUsed));
    vo.setSwapFree(formatFileSize(swapFree));

    // 从时序数据服务获取历史数据
    List<MemoryHistory> history = timeSeriesDataService.getMemoryHistory(parsePeriodHour(period));
    vo.setHistory(history);

    return vo;
  }

  @Override
  public DiskUsageVo getDiskUsage() {
    DiskUsageVo vo = new DiskUsageVo();
    List<DiskInfo> disks = new ArrayList<>();

    // 使用文件系统获取磁盘使用情况
    oshi.software.os.FileSystem fileSystem = os.getFileSystem();
    List<oshi.software.os.OSFileStore> fileStores = fileSystem.getFileStores();

    for (oshi.software.os.OSFileStore fileStore : fileStores) {
      DiskInfo diskInfo = new DiskInfo();
      diskInfo.setDevice(fileStore.getName());
      diskInfo.setMountPoint(fileStore.getMount());
      diskInfo.setFileSystem(fileStore.getType());

      long total = fileStore.getTotalSpace();
      long free = fileStore.getFreeSpace();
      long used = total - free;
      double usagePercent = total > 0 ? (double) used / total * 100 : 0;

      diskInfo.setTotal(formatFileSize(total));
      diskInfo.setUsed(formatFileSize(used));
      diskInfo.setFree(formatFileSize(free));
      diskInfo.setUsagePercent(formatPercent(usagePercent));

      disks.add(diskInfo);
    }

    vo.setDisks(disks);
    return vo;
  }

  @Override
  public NetworkUsageVo getNetworkUsage(String period) {
    NetworkUsageVo vo = new NetworkUsageVo();
    List<NetworkInterface> interfaces = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now();

    List<NetworkIF> networkIFs = systemInfo.getHardware().getNetworkIFs();

    // 首次调用时等待 1 秒后二次采样以得到有效速率
    if (previousNetworkSampleTime == null && !networkIFs.isEmpty()) {
      for (NetworkIF netIF : networkIFs) {
        netIF.updateAttributes();
      }
      previousNetworkSampleTime = now;
      for (NetworkIF netIF : networkIFs) {
        String key = netIF.getName();
        previousNetworkBytesByInterface.put(key,
            new long[]{netIF.getBytesRecv(), netIF.getBytesSent()});
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      return getNetworkUsage(period);
    }

    for (NetworkIF netIF : networkIFs) {
      netIF.updateAttributes();

      NetworkInterface networkInterface = new NetworkInterface();
      networkInterface.setName(netIF.getName());

      String[] ipv4Addrs = netIF.getIPv4addr();
      String ipAddress = (ipv4Addrs != null && ipv4Addrs.length > 0) ? ipv4Addrs[0] : "";
      if (ipAddress.isEmpty()) {
        String[] ipv6Addrs = netIF.getIPv6addr();
        if (ipv6Addrs != null && ipv6Addrs.length > 0) {
          ipAddress = ipv6Addrs[0];
        }
      }
      networkInterface.setIpAddress(ipAddress);

      networkInterface.setStatus(netIF.getSpeed() > 0 ? "UP" : "DOWN");
      networkInterface.setBytesIn(formatFileSize(netIF.getBytesRecv()));
      networkInterface.setBytesOut(formatFileSize(netIF.getBytesSent()));

      // getBytesRecv/Sent 为累计值，通过两次采样差分/时间计算 bytes/s
      long inRateBps = 0;
      long outRateBps = 0;
      long[] prev = previousNetworkBytesByInterface.get(netIF.getName());
      if (prev != null) {
        long elapsedSec = Math.max(1,
            java.time.Duration.between(previousNetworkSampleTime, now).getSeconds());
        inRateBps = Math.max(0, (netIF.getBytesRecv() - prev[0]) / elapsedSec);
        outRateBps = Math.max(0, (netIF.getBytesSent() - prev[1]) / elapsedSec);
      }
      previousNetworkBytesByInterface.put(netIF.getName(),
          new long[]{netIF.getBytesRecv(), netIF.getBytesSent()});
      previousNetworkSampleTime = now;

      networkInterface.setCurrentInRate(formatFileSize(inRateBps) + "/s");
      networkInterface.setCurrentOutRate(formatFileSize(outRateBps) + "/s");

      interfaces.add(networkInterface);
    }

    vo.setInterfaces(interfaces);

    // 从时序数据服务获取历史数据
    List<NetworkHistory> history = timeSeriesDataService.getNetworkHistory(parsePeriodHour(period));
    vo.setHistory(history);

    return vo;
  }

  @Override
  public List<ProcessInfoVo> getProcesses() {
    List<ProcessInfoVo> processes = new ArrayList<>();

    // 获取所有应用编码
    List<Application> applications = applicationQuery.findAll();
    List<String> appCodes = applications.stream()
        .map(Application::getCode)
        .collect(Collectors.toList());

    // 定义需要监控的系统进程名称（不区分大小写）
    //    List<String> systemProcessNames = DockerDetector.isRunningInDocker()
    //        ? List.of("angus", "mysqld", "mysql", "postgres", "nginx")
    //        : List.of("angus", "dockerd", "docker");
    // 只收集应用进程
    List<String> systemProcessNames = List.of("angus", "mysqld", "mysql", "postgres", "nginx");

    // 获取进程列表（限制数量以提高性能）
    int maxProcesses = 1000;
    List<OSProcess> osProcesses = os.getProcesses(
        OperatingSystem.ProcessFiltering.ALL_PROCESSES,
        OperatingSystem.ProcessSorting.CPU_DESC,
        maxProcesses);

    // 过滤进程：只保留包含应用编码的进程或系统进程
    List<OSProcess> filteredProcesses = osProcesses.stream()
        .filter(p -> {
          String processName = p.getName().toLowerCase();
          String commandLine = p.getCommandLine() != null ? p.getCommandLine().toLowerCase() : "";

          // 检查是否是系统进程
          boolean isSystemProcess = systemProcessNames.stream()
              .anyMatch(name -> processName.contains(name.toLowerCase()));

          // 检查是否包含应用编码
          boolean containsAppCode = appCodes.stream()
              .anyMatch(code -> processName.contains(code.toLowerCase())
                  || commandLine.contains(code.toLowerCase()));

          return isSystemProcess || containsAppCode;
        })
        .collect(Collectors.toList());

    // 为了计算CPU使用率，需要先获取一次进程快照
    Map<Integer, OSProcess> previousProcesses = new HashMap<>();
    for (OSProcess p : filteredProcesses) {
      previousProcesses.put(p.getProcessID(), p);
    }

    // 等待一小段时间后再次获取
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // 重新获取进程列表并应用相同的过滤
    List<OSProcess> currentProcesses = os.getProcesses(
        OperatingSystem.ProcessFiltering.ALL_PROCESSES,
        OperatingSystem.ProcessSorting.CPU_DESC,
        maxProcesses);

    List<OSProcess> currentFilteredProcesses = currentProcesses.stream()
        .filter(p -> {
          String processName = p.getName().toLowerCase();
          String commandLine = p.getCommandLine() != null ? p.getCommandLine().toLowerCase() : "";

          boolean isSystemProcess = systemProcessNames.stream()
              .anyMatch(name -> processName.contains(name.toLowerCase()));

          boolean containsAppCode = appCodes.stream()
              .anyMatch(code -> processName.contains(code.toLowerCase())
                  || commandLine.contains(code.toLowerCase()));

          return isSystemProcess || containsAppCode;
        })
        .collect(Collectors.toList());

    // 创建当前进程映射
    Map<Integer, OSProcess> currentProcessMap = new HashMap<>();
    for (OSProcess p : currentFilteredProcesses) {
      currentProcessMap.put(p.getProcessID(), p);
    }

    // 处理所有过滤后的进程
    for (OSProcess process : currentFilteredProcesses) {
      ProcessInfoVo vo = new ProcessInfoVo();
      vo.setPid((long) process.getProcessID());
      vo.setName(process.getName());
      vo.setUser(process.getUser());

      // CPU使用率（通过两次采样计算）
      OSProcess previousProcess = previousProcesses.get(process.getProcessID());
      double cpuPercent = 0.0;
      if (previousProcess != null) {
        cpuPercent = process.getProcessCpuLoadBetweenTicks(previousProcess) * 100;
      }
      vo.setCpuPercent(formatPercent(cpuPercent));

      // 内存使用率
      long rss = process.getResidentSetSize();
      vo.setMemoryPercent(
          formatPercent(memory.getTotal() > 0 ? (double) rss / memory.getTotal() * 100 : 0));
      vo.setMemoryUsage(formatFileSize(rss));
      vo.setStatus(process.getState().name());

      // 启动时间
      long startTime = process.getStartTime();
      if (startTime > 0) {
        vo.setStartTime(LocalDateTime.ofEpochSecond(startTime / 1000, 0,
            java.time.ZoneOffset.UTC));
      }

      vo.setCommand(process.getCommandLine());
      processes.add(vo);
    }

    return processes;
  }

  /**
   * 获取系统IP地址（优先IPv4，如果没有则使用IPv6）
   */
  private String getSystemIp() {
    try {
      List<NetworkIF> networkIFs = systemInfo.getHardware().getNetworkIFs();
      for (NetworkIF netIF : networkIFs) {
        netIF.updateAttributes();
        // 优先获取IPv4地址
        String[] ipv4Addrs = netIF.getIPv4addr();
        if (ipv4Addrs != null && ipv4Addrs.length > 0) {
          // 跳过回环地址
          for (String ip : ipv4Addrs) {
            if (ip != null && !ip.isEmpty() && !ip.startsWith("127.") && !ip.equals("0.0.0.0")) {
              return ip;
            }
          }
        }
      }
      // 如果没有找到IPv4，尝试IPv6
      for (NetworkIF netIF : networkIFs) {
        netIF.updateAttributes();
        String[] ipv6Addrs = netIF.getIPv6addr();
        if (ipv6Addrs != null && ipv6Addrs.length > 0) {
          // 跳过回环地址和链路本地地址
          for (String ip : ipv6Addrs) {
            if (ip != null && !ip.isEmpty() && !ip.startsWith("::1") && !ip.startsWith("fe80:")) {
              return ip;
            }
          }
        }
      }
    } catch (Exception e) {
      // 如果获取失败，返回空字符串
    }
    return "";
  }

}
