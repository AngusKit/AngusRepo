package cloud.xcan.angus.core.gm.infra.job;

import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationSource;
import cloud.xcan.angus.core.gm.application.cmd.log.SystemLogCmd;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.domain.log.SystemLog;
import cloud.xcan.angus.core.gm.domain.log.SystemLogRepo;
import cloud.xcan.angus.core.gm.domain.log.enums.LogStatus;
import cloud.xcan.angus.core.gm.domain.log.enums.LogType;
import cloud.xcan.angus.core.job.JobTemplate;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.idgen.uid.impl.CachedUidGenerator;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 同步应用日志文件信息任务
 */
@Slf4j
@Component
public class LogFileSyncJob {

  private static final String LOCK_KEY = "gm:job:LogFileSyncJob";

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private ApplicationQuery applicationQuery;

  @Resource
  private SystemLogCmd systemLogCmd;

  @Resource
  private SystemLogRepo systemLogRepo;

  @Resource
  private ApplicationInfo applicationInfo;

  @Resource
  protected CachedUidGenerator uidGenerator;

  @Scheduled(fixedDelay = 3 * 60 * 1000, initialDelay = 5000)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 5, TimeUnit.MINUTES, () -> {
      try {
        log.info("开始同步应用日志文件信息");

        // 1. 查询所有应用
        List<Application> apps = applicationQuery.findAllByType(ApplicationSource.INSTALLED);
        if (apps.isEmpty()) {
          log.info("未找到应用，跳过日志文件同步");
          return;
        }

        // 2. 批量查询所有应用的现有日志记录（用于去重，考虑实例维度）
        // Map结构：applicationId -> instanceId -> filePath -> SystemLog
        Map<Long, Map<String, Map<String, SystemLog>>> existingLogsMap = new HashMap<>();
        String currentInstanceId = applicationInfo.getInstanceId();
        for (Application app : apps) {
          try {
            List<SystemLog> existingLogs = systemLogRepo.findByApplicationId(app.getId());
            Map<String, Map<String, SystemLog>> instanceMap = new HashMap<>();
            for (SystemLog log : existingLogs) {
              String instanceId = log.getInstanceId();
              if (instanceId == null || instanceId.isEmpty()) {
                // 如果没有实例ID，使用当前实例ID（兼容旧数据）
                instanceId = currentInstanceId;
              }
              instanceMap.computeIfAbsent(instanceId, k -> new HashMap<>())
                  .put(log.getFilePath(), log);
            }
            existingLogsMap.put(app.getId(), instanceMap);
          } catch (Exception e) {
            log.warn("查询应用「{}」的现有日志记录失败", app.getName(), e);
            existingLogsMap.put(app.getId(), new HashMap<>());
          }
        }

        List<SystemLog> systemLogs = new ArrayList<>();
        List<Long> idsToDelete = new ArrayList<>();
        int processedApps = 0;
        int processedFiles = 0;

        // 3. 遍历每个应用，读取日志文件
        for (Application app : apps) {
          if (!StringUtils.hasText(app.getInstalledPath())) {
            log.debug("应用「{}」未设置安装路径，跳过", app.getName());
            continue;
          }

          try {
            Path logsDir = Paths.get(app.getInstalledPath(), "logs");
            if (!Files.exists(logsDir) || !Files.isDirectory(logsDir)) {
              log.debug("应用「{}」的日志目录不存在：{}", app.getName(), logsDir);
              continue;
            }

            // 提取应用简写编码
            String appShortCode = extractAppShortCode(app.getCode());

            // 扫描日志目录
            Map<String, Map<String, SystemLog>> appInstanceLogsMap =
                existingLogsMap.getOrDefault(app.getId(), new HashMap<>());
            ScanResult scanResult =
                scanLogDirectory(logsDir, app, appShortCode, appInstanceLogsMap);
            systemLogs.addAll(scanResult.systemLogs);
            processedFiles += scanResult.systemLogs.size();
            processedApps++;

            // 清理磁盘上已删除的日志对应的数据库记录（仅当前实例）
            Map<String, SystemLog> currentInstanceLogs =
                appInstanceLogsMap.getOrDefault(currentInstanceId, new HashMap<>());
            for (Map.Entry<String, SystemLog> entry : currentInstanceLogs.entrySet()) {
              String filePath = entry.getKey();
              if (!scanResult.foundFilePaths.contains(filePath)) {
                idsToDelete.add(entry.getValue().getId());
              }
            }

          } catch (Exception e) {
            log.error("处理应用「{}」的日志文件失败", app.getName(), e);
          }
        }

        // 4. 删除磁盘上已不存在的日志记录
        if (!idsToDelete.isEmpty()) {
          try {
            systemLogCmd.batchDelete(idsToDelete, true);
            log.info("已清理 {} 条磁盘上已删除的日志记录", idsToDelete.size());
          } catch (Exception e) {
            log.warn("清理已删除日志记录失败", e);
          }
        }

        // 5. 批量保存系统日志文件记录
        if (!systemLogs.isEmpty()) {
          systemLogCmd.batchSaveOrUpdate(systemLogs);
          log.info("同步完成：处理了 {} 个应用，共 {} 个日志文件", processedApps, processedFiles);
        } else {
          log.info("未发现新的日志文件");
        }

      } catch (Exception e) {
        log.error("同步应用日志文件信息失败", e);
      }
    });
  }

  /**
   * 扫描日志目录，解析日志文件信息
   *
   * @return 扫描结果，包含待保存的日志列表和磁盘上存在的文件路径集合
   */
  private ScanResult scanLogDirectory(Path logsDir, Application application,
      String appShortCode, Map<String, Map<String, SystemLog>> existingLogsMap) throws IOException {
    List<SystemLog> systemLogs = new ArrayList<>();
    Set<String> foundFilePaths = new HashSet<>();
    String currentInstanceId = applicationInfo.getInstanceId();

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDir)) {
      for (Path filePath : stream) {
        if (!Files.isRegularFile(filePath)) {
          continue;
        }

        String filename = filePath.getFileName().toString();
        if (!isLogFile(filename)) {
          continue;
        }

        try {
          SystemLog systemLog = parseLogFile(filePath, filename, application, appShortCode);
          String instanceId = systemLog.getInstanceId();
          if (instanceId == null || instanceId.isEmpty()) {
            instanceId = currentInstanceId;
            systemLog.setInstanceId(instanceId);
          }

          String pathStr = systemLog.getFilePath();
          foundFilePaths.add(pathStr);

          // 检查是否已存在（根据实例ID和文件路径）
          Map<String, SystemLog> instanceLogs = existingLogsMap.get(instanceId);
          SystemLog existing = null;
          if (instanceLogs != null) {
            existing = instanceLogs.get(pathStr);
          }

          if (existing != null) {
            // 更新现有记录
            existing.setSize(systemLog.getSize());
            existing.setLineCount(systemLog.getLineCount());
            existing.setStatus(systemLog.getStatus());
            existing.setDate(systemLog.getDate());
            existing.setType(systemLog.getType());
            existing.setInstanceId(instanceId); // 确保实例ID已设置
            systemLogs.add(existing);
          } else {
            // 新增记录
            systemLogs.add(systemLog);
          }
        } catch (Exception e) {
          log.warn("解析日志文件失败：{}", filePath, e);
        }
      }
    }
    return new ScanResult(systemLogs, foundFilePaths);
  }

  /**
   * 判断是否为日志文件
   */
  private boolean isLogFile(String filename) {
    return filename.endsWith(".log") || filename.endsWith(".log.gz");
  }

  /**
   * 解析日志文件信息
   */
  private SystemLog parseLogFile(Path filePath, String filename, Application application,
      String appShortCode) throws IOException {
    SystemLog systemLog = new SystemLog();
    systemLog.setId(uidGenerator.getUID());
    systemLog.setFilename(filename);
    systemLog.setFilePath(filePath.toString());
    systemLog.setApplicationId(application.getId());
    systemLog.setServiceCode("XCAN-" + application.getCode().toUpperCase() + ".BOOT");
    systemLog.setServiceName(systemLog.getServiceName());
    systemLog.setInstanceId(applicationInfo.getInstanceId());

    // 获取文件大小
    long fileSize = Files.size(filePath);
    systemLog.setSize(fileSize);

    // 判断是否压缩
    boolean compressed = filename.endsWith(".gz");
    systemLog.setCompressed(compressed);

    // 解析日志类型和日期
    LogFileInfo fileInfo = parseLogFilename(filename, appShortCode);
    systemLog.setType(fileInfo.type);
    systemLog.setDate(fileInfo.date);
    systemLog.setStatus(fileInfo.status);

    // 计算行数（仅对未压缩的文件）
    if (!compressed && fileSize < 200 * 1024 * 1024) { // 小于200MB的文件才计算行数
      try {
        long lineCount = countLines(filePath);
        systemLog.setLineCount(lineCount);
      } catch (Exception e) {
        log.debug("计算文件行数失败：{}", filePath, e);
      }
    }

    systemLog.setCreatedDate(LocalDateTime.now());
    return systemLog;
  }

  /**
   * 解析日志文件名，提取类型和日期信息
   */
  private LogFileInfo parseLogFilename(String filename, String appShortCode) {
    LogFileInfo info = new LogFileInfo();

    // 转义应用简写编码中的特殊字符，用于正则表达式
    String escapedAppShortCode = Pattern.quote(appShortCode);

    // 匹配带日期的应用日志：{appShortCode}-2026-01-01.0.log
    Pattern dateLogPattern = Pattern.compile(
        escapedAppShortCode + "-(\\d{4}-\\d{2}-\\d{2})\\.(\\d+)\\.log");
    Matcher dateLogMatcher = dateLogPattern.matcher(filename);
    if (dateLogMatcher.find()) {
      info.type = LogType.APPLICATION;
      info.status = LogStatus.COMPLETED;
      try {
        info.date = LocalDate.parse(dateLogMatcher.group(1), DATE_FORMATTER);
      } catch (DateTimeParseException e) {
        info.date = LocalDate.now();
      }
      return info;
    }

    // 匹配带日期的错误日志：{appShortCode}-2026-01-01.0.error.log
    Pattern dateErrorLogPattern = Pattern.compile(
        escapedAppShortCode + "-(\\d{4}-\\d{2}-\\d{2})\\.(\\d+)\\.error\\.log");
    Matcher dateErrorLogMatcher = dateErrorLogPattern.matcher(filename);
    if (dateErrorLogMatcher.find()) {
      info.type = LogType.ERROR;
      info.status = LogStatus.COMPLETED;
      try {
        info.date = LocalDate.parse(dateErrorLogMatcher.group(1), DATE_FORMATTER);
      } catch (DateTimeParseException e) {
        info.date = LocalDate.now();
      }
      return info;
    }

    // 匹配控制台日志：{appShortCode}-console.log
    Pattern consoleLogPattern = Pattern.compile(escapedAppShortCode + "-console\\.log");
    if (consoleLogPattern.matcher(filename).find()) {
      info.type = LogType.CONSOLE;
      info.status = LogStatus.ACTIVE;
      info.date = LocalDate.now();
      return info;
    }

    // 匹配当前应用日志：{appShortCode}.log
    Pattern currentLogPattern = Pattern.compile(escapedAppShortCode + "\\.log");
    if (currentLogPattern.matcher(filename).find()) {
      info.type = LogType.APPLICATION;
      info.status = LogStatus.ACTIVE;
      info.date = LocalDate.now();
      return info;
    }

    // 匹配当前错误日志：{appShortCode}.error.log
    Pattern currentErrorLogPattern = Pattern.compile(escapedAppShortCode + "\\.error\\.log");
    if (currentErrorLogPattern.matcher(filename).find()) {
      info.type = LogType.ERROR;
      info.status = LogStatus.ACTIVE;
      info.date = LocalDate.now();
      return info;
    }

    // 其他日志文件
    info.type = LogType.OTHER;
    info.status = LogStatus.COMPLETED;
    info.date = LocalDate.now();
    return info;
  }

  /**
   * 计算文件行数（优化版本，避免加载整个文件到内存）
   */
  private long countLines(Path filePath) throws IOException {
    try (Stream<String> lines = Files.lines(filePath)) {
      return lines.count();
    }
  }

  /**
   * 提取应用简写编码（应用编码删除Angus前缀后小写）
   */
  private String extractAppShortCode(String appCode) {
    if (!StringUtils.hasText(appCode)) {
      return "gm"; // 默认值
    }

    String shortCode = appCode;
    // 删除Angus前缀（不区分大小写）
    if (shortCode.length() > 5 && shortCode.substring(0, 5).equalsIgnoreCase("Angus")) {
      shortCode = shortCode.substring(5);
    }

    // 转为小写
    return shortCode.toLowerCase();
  }

  /**
   * 日志文件信息
   */
  private static class LogFileInfo {

    LogType type;
    LocalDate date;
    LogStatus status;
  }

  /**
   * 扫描结果，包含待保存的日志列表和磁盘上存在的文件路径集合
   */
  private static class ScanResult {

    final List<SystemLog> systemLogs;
    final Set<String> foundFilePaths;

    ScanResult(List<SystemLog> systemLogs, Set<String> foundFilePaths) {
      this.systemLogs = systemLogs;
      this.foundFilePaths = foundFilePaths;
    }
  }
}
