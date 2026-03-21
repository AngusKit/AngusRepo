package cloud.xcan.angus.core.gm.infra.backup;

import cloud.xcan.angus.api.commonlink.application.ApplicationRepo;
import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.BackupRepo;
import cloud.xcan.angus.core.gm.domain.backup.RestoreOptions;
import cloud.xcan.angus.core.gm.domain.backup.RestoreTask;
import cloud.xcan.angus.core.gm.domain.backup.enums.RestoreSource;
import cloud.xcan.angus.core.jdbc.JDBCUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Enumeration;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 恢复服务
 */
@Slf4j
@Service
public class RestoreService {

  @Resource
  private BackupRepo backupRepo;

  @Resource
  private ApplicationRepo applicationRepo;

  @Resource
  @Qualifier("dataSource")
  private DataSource dataSource;

  @Resource
  private BackupServiceHelper backupHelper;

  @Resource
  private ObjectMapper objectMapper;

  @Value("${xcan.datasource.extra.dbType:MYSQL}")
  private String dbType;

  /**
   * 执行恢复任务
   */
  public void executeRestore(RestoreTask restoreTask) throws Exception {
    log.info("开始执行恢复任务：{}", restoreTask.getId());

    String backupFilePath = getBackupFilePath(restoreTask);
    if (backupFilePath == null || !new File(backupFilePath).exists()) {
      throw new RuntimeException("备份文件不存在：" + backupFilePath);
    }

    // 创建临时解压目录
    String tempDir = System.getProperty("java.io.tmpdir");
    String restoreTempDir = Paths.get(tempDir, "restore_" + restoreTask.getId() + "_" +
            LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")))
        .toString();
    Files.createDirectories(Paths.get(restoreTempDir));

    try {
      // 1. 解压备份文件
      updateProgress(restoreTask, 10, "正在解压备份文件...");
      extractBackupFile(backupFilePath, restoreTempDir);

      // 2. 读取manifest.json
      updateProgress(restoreTask, 20, "正在读取备份清单...");
      BackupManifest manifest = readManifest(restoreTempDir);
      if (manifest == null) {
        throw new RuntimeException("无法读取备份清单文件");
      }

      // 检查备份类型，如果是增量备份，给出警告提示
      if ("INCREMENTAL".equalsIgnoreCase(manifest.getBackupType())) {
        log.warn("正在恢复增量备份「{}」，请确保已先恢复对应的全量备份（备份ID：{}）",
            manifest.getBackupName(), manifest.getLastBackupId());
      }

      // 检查数据库类型一致性
      if (manifest.getDbType() != null && !manifest.getDbType().equalsIgnoreCase(dbType)) {
        log.warn("备份的数据库类型（{}）与当前系统数据库类型（{}）不一致，可能导致恢复失败",
            manifest.getDbType(), dbType);
      }

      // 3. 恢复数据库
      if (restoreTask.getOptions().getRestoreDatabase()) {
        updateProgress(restoreTask, 30, "正在恢复数据库...");
        restoreDatabase(restoreTempDir, manifest);
        updateProgress(restoreTask, 50, "数据库恢复完成");
      }

      // 4. 恢复应用文件
      if (manifest.getApplications() != null && !manifest.getApplications().isEmpty()) {
        int totalApps = manifest.getApplications().size();
        int appIndex = 0;
        for (ApplicationBackupInfo appInfo : manifest.getApplications()) {
          appIndex++;
          int progress = 50 + (appIndex * 40 / totalApps);
          updateProgress(restoreTask, progress,
              String.format("正在恢复应用「%s」(%d/%d)...", appInfo.getApplicationName(), appIndex,
                  totalApps));

          try {
            restoreApplicationFiles(restoreTempDir, appInfo, restoreTask.getOptions(), manifest);
          } catch (Exception e) {
            log.error("恢复应用「{}」失败：{}", appInfo.getApplicationName(), e.getMessage(), e);
            // 单个应用恢复失败不影响其他应用，继续处理下一个
          }
        }
      }

      updateProgress(restoreTask, 100, "恢复完成");
      log.info("恢复任务「{}」执行成功", restoreTask.getId());

    } finally {
      // 清理临时目录
      backupHelper.deleteDirectory(Paths.get(restoreTempDir));
    }
  }

  /**
   * 获取备份文件路径
   */
  private String getBackupFilePath(RestoreTask restoreTask) {
    if (restoreTask.getSource() == RestoreSource.BACKUP) {
      // 从备份列表恢复
      Backup backup = backupRepo.findById(restoreTask.getBackupId())
          .orElseThrow(() -> new RuntimeException("备份不存在：" + restoreTask.getBackupId()));
      return backup.getBackupPath();
    } else if (restoreTask.getSource() == RestoreSource.FILE_PATH) {
      // 从文件路径恢复
      return restoreTask.getFilePath();
    } else {
      throw new RuntimeException("不支持的恢复源类型：" + restoreTask.getSource());
    }
  }

  /**
   * 解压备份文件
   */
  private void extractBackupFile(String backupFilePath, String extractDir) throws Exception {
    log.info("开始解压备份文件：{} 到 {}", backupFilePath, extractDir);

    try (java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(backupFilePath)) {
      Enumeration<? extends java.util.zip.ZipEntry> entries = zipFile.entries();

      while (entries.hasMoreElements()) {
        java.util.zip.ZipEntry entry = entries.nextElement();
        String entryName = entry.getName();
        Path entryPath = Paths.get(extractDir, entryName);

        // 跳过manifest.json（稍后单独处理）
        if (entryName.equals("manifest.json")) {
          continue;
        }

        if (entry.isDirectory()) {
          Files.createDirectories(entryPath);
        } else {
          Files.createDirectories(entryPath.getParent());
          try (java.io.InputStream is = zipFile.getInputStream(entry);
              java.io.FileOutputStream fos = new java.io.FileOutputStream(entryPath.toFile())) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
              fos.write(buffer, 0, bytesRead);
            }
          }
        }
      }

      // 单独提取manifest.json
      java.util.zip.ZipEntry manifestEntry = zipFile.getEntry("manifest.json");
      if (manifestEntry != null) {
        Path manifestPath = Paths.get(extractDir, "manifest.json");
        try (java.io.InputStream is = zipFile.getInputStream(manifestEntry);
            java.io.FileOutputStream fos = new java.io.FileOutputStream(manifestPath.toFile())) {
          byte[] buffer = new byte[8192];
          int bytesRead;
          while ((bytesRead = is.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
          }
        }
      }

      log.info("备份文件解压完成");
    }
  }

  /**
   * 读取manifest.json
   */
  private BackupManifest readManifest(String extractDir) throws Exception {
    String manifestFile = Paths.get(extractDir, "manifest.json").toString();
    File file = new File(manifestFile);
    if (!file.exists()) {
      log.error("备份清单文件不存在：{}", manifestFile);
      return null;
    }

    return objectMapper.readValue(file, BackupManifest.class);
  }

  /**
   * 恢复数据库
   */
  private void restoreDatabase(String extractDir, BackupManifest manifest) throws Exception {
    String dbBackupFile = manifest.getDatabaseBackupFile();
    if (dbBackupFile == null || dbBackupFile.trim().isEmpty()) {
      log.warn("备份清单中未指定数据库备份文件，跳过数据库恢复");
      return;
    }

    String sqlFile = Paths.get(extractDir, dbBackupFile).toString();
    File file = new File(sqlFile);
    if (!file.exists()) {
      log.error("数据库备份文件不存在：{}", sqlFile);
      throw new RuntimeException("数据库备份文件不存在：" + sqlFile);
    }

    log.info("开始恢复数据库，SQL文件：{}", sqlFile);

    // 读取SQL文件内容
    String sqlScript = readSqlFile(sqlFile);

    // 执行SQL脚本
    try (Connection connection = dataSource.getConnection()) {
      // MySQL和PostgreSQL都支持多语句执行
      JDBCUtils.executeScript(connection, sqlScript, Collections.emptyMap());
      log.info("数据库恢复完成");
    } catch (Exception e) {
      log.error("数据库恢复失败", e);
      throw new RuntimeException("数据库恢复失败：" + e.getMessage(), e);
    }
  }

  /**
   * 读取SQL文件内容
   */
  private String readSqlFile(String sqlFile) throws IOException {
    StringBuilder sqlScript = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(
        new FileReader(sqlFile, java.nio.charset.StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        sqlScript.append(line).append("\n");
      }
    }
    return sqlScript.toString();
  }

  /**
   * 恢复应用文件
   */
  private void restoreApplicationFiles(String extractDir, ApplicationBackupInfo appInfo,
      RestoreOptions options, BackupManifest manifest) throws Exception {
    log.info("开始恢复应用「{}」的文件", appInfo.getApplicationName());

    // 查找应用实体
    var applicationOpt = applicationRepo.findById(appInfo.getApplicationId());
    if (applicationOpt.isEmpty()) {
      log.warn("应用「{}」(ID: {})不存在，跳过文件恢复",
          appInfo.getApplicationName(), appInfo.getApplicationId());
      return;
    }

    var application = applicationOpt.get();
    String installedPath = application.getInstalledPath();
    if (!StringUtils.hasText(installedPath)) {
      log.warn("应用「{}」未配置安装路径，跳过文件恢复", appInfo.getApplicationName());
      return;
    }

    File appDir = new File(installedPath);
    if (!appDir.exists() || !appDir.isDirectory()) {
      log.warn("应用「{}」安装路径不存在：{}，跳过文件恢复",
          appInfo.getApplicationName(), installedPath);
      return;
    }

    // 恢复conf目录（conf目录总是全量备份，直接替换）
    if (options.getRestoreConfig() && appInfo.getConfBackedUp() && appInfo.getConfPath() != null) {
      try {
        String confSourcePath = Paths.get(extractDir, appInfo.getConfPath()).toString();
        String confTargetPath = Paths.get(installedPath, "conf").toString();
        copyDirectory(Paths.get(confSourcePath), Paths.get(confTargetPath), false);
        log.info("应用「{}」的conf目录恢复完成", appInfo.getApplicationName());
      } catch (Exception e) {
        log.error("恢复应用「{}」的conf目录失败：{}", appInfo.getApplicationName(), e.getMessage(), e);
        throw e;
      }
    }

    // 恢复data目录（如果是增量备份，需要合并文件；如果是全量备份，直接替换）
    if (options.getRestoreFiles() && appInfo.getDataBackedUp() && appInfo.getDataPath() != null) {
      try {
        String dataSourcePath = Paths.get(extractDir, appInfo.getDataPath()).toString();
        String dataTargetPath = Paths.get(installedPath, "data").toString();
        // 检查备份类型，增量备份需要合并文件
        boolean isIncremental = "INCREMENTAL".equalsIgnoreCase(manifest.getBackupType());
        copyDirectory(Paths.get(dataSourcePath), Paths.get(dataTargetPath), isIncremental);
        log.info("应用「{}」的data目录恢复完成（{}）", appInfo.getApplicationName(),
            isIncremental ? "增量合并" : "全量替换");
      } catch (Exception e) {
        log.error("恢复应用「{}」的data目录失败：{}", appInfo.getApplicationName(), e.getMessage(), e);
        throw e;
      }
    }

    // 恢复logs目录（logs目录总是全量备份，直接替换）
    if (options.getRestoreLogs() && appInfo.getLogsBackedUp() && appInfo.getLogsPath() != null) {
      try {
        String logsSourcePath = Paths.get(extractDir, appInfo.getLogsPath()).toString();
        String logsTargetPath = Paths.get(installedPath, "logs").toString();
        copyDirectory(Paths.get(logsSourcePath), Paths.get(logsTargetPath), false);
        log.info("应用「{}」的logs目录恢复完成", appInfo.getApplicationName());
      } catch (Exception e) {
        log.error("恢复应用「{}」的logs目录失败：{}", appInfo.getApplicationName(), e.getMessage(), e);
        throw e;
      }
    }

    log.info("应用「{}」的文件恢复完成", appInfo.getApplicationName());
  }

  /**
   * 复制目录
   *
   * @param source    源目录
   * @param target    目标目录
   * @param mergeMode 是否为合并模式（true：增量备份合并，false：全量备份替换）
   */
  private void copyDirectory(Path source, Path target, boolean mergeMode) throws IOException {
    if (!Files.exists(source) || !Files.isDirectory(source)) {
      log.warn("源目录不存在或不是目录：{}", source);
      return;
    }

    Files.createDirectories(target);

    Files.walk(source).forEach(sourcePath -> {
      try {
        Path relativePath = source.relativize(sourcePath);
        Path targetPath = target.resolve(relativePath);

        if (Files.isDirectory(sourcePath)) {
          Files.createDirectories(targetPath);
        } else {
          // 如果是合并模式且目标文件已存在，跳过（增量备份只恢复新增/修改的文件）
          if (mergeMode && Files.exists(targetPath)) {
            // 增量备份：只恢复备份中包含的文件，已存在的文件保持不变
            // 这里直接覆盖，因为备份中只包含需要更新的文件
            Files.copy(sourcePath, targetPath,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                java.nio.file.StandardCopyOption.COPY_ATTRIBUTES);
          } else {
            // 全量备份：直接替换
            Files.copy(sourcePath, targetPath,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                java.nio.file.StandardCopyOption.COPY_ATTRIBUTES);
          }

          // 尝试保留文件的修改时间
          try {
            Files.setLastModifiedTime(targetPath, Files.getLastModifiedTime(sourcePath));
          } catch (Exception e) {
            log.debug("无法设置文件修改时间：{}", targetPath, e);
          }
        }
      } catch (IOException e) {
        log.error("复制文件失败：{} -> {}", sourcePath, target, e);
        throw new RuntimeException("复制文件失败：" + sourcePath, e);
      }
    });
  }

  /**
   * 更新恢复任务进度
   */
  private void updateProgress(RestoreTask restoreTask, int progress, String currentStep) {
    restoreTask.setProgress(progress);
    restoreTask.setCurrentStep(currentStep);
    if (restoreTask.getCompletedSteps() == null) {
      restoreTask.setCompletedSteps(0);
    }
    // 根据进度估算已完成步骤数
    if (restoreTask.getTotalSteps() != null && restoreTask.getTotalSteps() > 0) {
      restoreTask.setCompletedSteps((int) (progress * restoreTask.getTotalSteps() / 100.0));
    }
    log.debug("恢复任务「{}」进度：{}%，当前步骤：{}", restoreTask.getId(), progress, currentStep);
  }
}
