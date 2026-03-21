package cloud.xcan.angus.core.gm.infra.backup;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.commonlink.application.ApplicationRepo;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.BackupRepo;
import cloud.xcan.angus.core.gm.domain.backup.enums.BackupType;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.search.SearchCriteria;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 备份服务
 */
@Slf4j
@Service
public class BackupService {

  @Resource
  private ApplicationRepo applicationRepo;

  @Resource
  private BackupRepo backupRepo;

  @PersistenceContext
  private EntityManager entityManager;

  @Resource
  @Qualifier("dataSource")
  private DataSource dataSource;

  @Resource
  private Environment environment;

  @Resource
  private SettingManager settingManager;

  @Resource
  private BackupServiceHelper backupHelper;

  @Resource
  private ObjectMapper objectMapper;

  @Value("${xcan.datasource.extra.dbType:MYSQL}")
  private String dbType;

  /**
   * 执行备份
   */
  public void executeBackup(Backup backup) throws Exception {
    log.info("开始执行备份任务：{}，类型：{}", backup.getName(), backup.getType());

    // 创建临时备份目录
    String tempDir = System.getProperty("java.io.tmpdir");
    String backupTempDir = Paths.get(tempDir, "backup_" + backup.getId() + "_" +
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))).toString();
    Files.createDirectories(Paths.get(backupTempDir));

    try {
      // 1. 获取要备份的应用列表
      List<Application> applications = getApplicationsToBackup(backup);
      log.info("需要备份的应用数量：{}", applications.size());

      // 2. 备份清单
      BackupManifest manifest = new BackupManifest();
      manifest.setBackupId(backup.getId());
      manifest.setBackupName(backup.getName());
      manifest.setBackupType(backup.getType().name());
      manifest.setBackupTime(LocalDateTime.now());
      manifest.setDbType(dbType);

      // 3. 备份数据库
      try {
        if (backup.getType() == BackupType.FULL) {
          backupDatabase(backupTempDir, manifest);
        } else {
          backupDatabaseIncremental(backupTempDir, manifest, backup);
        }
      } catch (Exception e) {
        log.error("数据库备份失败：{}", e.getMessage(), e);
        manifest.addError("数据库备份失败：" + e.getMessage());
        // 当前策略：数据库备份失败则整个备份失败
        throw e;
      }

      // 4. 备份应用文件
      List<ApplicationBackupInfo> appBackupInfos = new ArrayList<>();
      for (Application application : applications) {
        try {
          ApplicationBackupInfo appInfo = backupApplicationFiles(application, backupTempDir,
              backup.getType(), backup);
          if (appInfo != null) {
            appBackupInfos.add(appInfo);
          }
        } catch (Exception e) {
          log.error("备份应用「{}」失败：{}", application.getName(), e.getMessage(), e);
          manifest.addError("应用「" + application.getName() + "」备份失败：" + e.getMessage());
        }
      }
      manifest.setApplications(appBackupInfos);

      // 5. 保存清单文件
      saveManifest(backupTempDir, manifest);

      // 6. 打包备份文件
      String backupFilePath = backup.getBackupPath();
      backupHelper.createBackupArchive(backupTempDir, backupFilePath);

      // 7. 设置备份文件大小
      File backupFile = new File(backupFilePath);
      if (backupFile.exists()) {
        backup.setFileSize(backupFile.length());
      }

      log.info("备份任务「{}」执行完成，备份文件：{}，大小：{} 字节", backup.getName(),
          backupFilePath, backup.getFileSize());

    } finally {
      // 清理临时目录
      backupHelper.deleteDirectory(Paths.get(backupTempDir));
    }
  }

  /**
   * 获取要备份的应用列表 只备份已启用状态的应用
   */
  private List<Application> getApplicationsToBackup(Backup backup) {
    if (backup.getApplicationId() != null) {
      // 备份指定应用，但只备份已启用的应用
      return applicationRepo.findById(backup.getApplicationId())
          .filter(app -> app.getStatus() == EnabledStatus.ENABLED)
          .map(List::of)
          .orElse(List.of());
    } else {
      // 备份所有已启用的应用
      GenericSpecification<Application> spec = new GenericSpecification<>();
      spec.getCriteria().add(
          new SearchCriteria("status", EnabledStatus.ENABLED,
              cloud.xcan.angus.remote.search.SearchOperation.EQUAL));
      return applicationRepo.findAll(spec);
    }
  }

  /**
   * 备份数据库（全量）
   */
  private void backupDatabase(String backupDir, BackupManifest manifest) throws Exception {
    log.info("开始备份数据库（全量）");

    String dbBackupFile = Paths.get(backupDir, "database", "full_backup.sql").toString();
    Files.createDirectories(Paths.get(dbBackupFile).getParent());

    // 获取数据库连接信息
    BackupServiceHelper.DatabaseConnectionInfo dbInfo = backupHelper.getDatabaseConnectionInfo();

    if ("MYSQL".equalsIgnoreCase(dbType)) {
      backupHelper.backupMySQLDatabase(dbInfo, dbBackupFile);
    } else if ("POSTGRES".equalsIgnoreCase(dbType)) {
      backupHelper.backupPostgreSQLDatabase(dbInfo, dbBackupFile);
    } else {
      throw new UnsupportedOperationException("不支持的数据库类型：" + dbType);
    }

    manifest.setDatabaseBackupFile("database/full_backup.sql");

    // 记录当前最大ID
    Long currentMaxId = backupHelper.getCurrentMaxId();
    manifest.setCurrentMaxId(currentMaxId);

    log.info("数据库备份完成：{}，当前最大ID：{}", dbBackupFile, currentMaxId);
  }

  /**
   * 备份数据库（增量）
   */
  private void backupDatabaseIncremental(String backupDir, BackupManifest manifest, Backup backup)
      throws Exception {
    log.info("开始备份数据库（增量）");

    // 查找上一次备份
    Backup lastBackup = findLastFullBackup(backup);
    if (lastBackup == null) {
      log.warn("未找到上一次全量备份，执行全量备份");
      backupDatabase(backupDir, manifest);
      return;
    }

    // 获取上次备份的最大ID
    Long lastMaxId = backupHelper.getLastBackupMaxId(lastBackup);
    if (lastMaxId == null) {
      log.warn("无法获取上次备份的最大ID，执行全量备份");
      backupDatabase(backupDir, manifest);
      return;
    }

    String dbBackupFile = Paths.get(backupDir, "database", "incremental_backup.sql").toString();
    Files.createDirectories(Paths.get(dbBackupFile).getParent());

    BackupServiceHelper.DatabaseConnectionInfo dbInfo = backupHelper.getDatabaseConnectionInfo();

    if ("MYSQL".equalsIgnoreCase(dbType)) {
      backupHelper.backupMySQLDatabaseIncremental(dbInfo, dbBackupFile, lastMaxId);
    } else if ("POSTGRES".equalsIgnoreCase(dbType)) {
      backupHelper.backupPostgreSQLDatabaseIncremental(dbInfo, dbBackupFile, lastMaxId);
    } else {
      throw new UnsupportedOperationException("不支持的数据库类型：" + dbType);
    }

    manifest.setDatabaseBackupFile("database/incremental_backup.sql");
    manifest.setLastBackupId(lastBackup.getId());
    manifest.setLastBackupMaxId(lastMaxId);

    // 记录当前最大ID
    Long currentMaxId = backupHelper.getCurrentMaxId();
    manifest.setCurrentMaxId(currentMaxId);

    log.info("数据库增量备份完成：{}，上次最大ID：{}，当前最大ID：{}", dbBackupFile, lastMaxId,
        currentMaxId);
  }

  /**
   * 查找上一次全量备份
   */
  Backup findLastFullBackup(Backup currentBackup) {
    List<Backup> fullBackups = backupRepo.findByType(BackupType.FULL);
    return fullBackups.stream()
        .filter(b -> b.getStatus()
            == cloud.xcan.angus.core.gm.domain.backup.enums.BackupStatus.COMPLETED)
        .filter(b -> b.getId() < currentBackup.getId())
        .max((a, b) -> {
          if (a.getCreatedDate() == null || b.getCreatedDate() == null) {
            return 0;
          }
          return a.getCreatedDate().compareTo(b.getCreatedDate());
        })
        .orElse(null);
  }

  /**
   * 备份应用文件
   */
  private ApplicationBackupInfo backupApplicationFiles(Application application, String backupDir,
      BackupType backupType, Backup backup) throws Exception {
    String installedPath = application.getInstalledPath();
    if (!StringUtils.hasText(installedPath)) {
      log.warn("应用「{}」未配置安装路径，跳过备份", application.getName());
      return null;
    }

    File appDir = new File(installedPath);
    if (!appDir.exists() || !appDir.isDirectory()) {
      log.warn("应用「{}」安装路径不存在：{}，跳过备份", application.getName(), installedPath);
      return null;
    }

    log.info("开始备份应用「{}」，路径：{}", application.getName(), installedPath);

    ApplicationBackupInfo appInfo = new ApplicationBackupInfo();
    appInfo.setApplicationId(application.getId());
    appInfo.setApplicationName(application.getName());
    appInfo.setApplicationCode(application.getCode());
    appInfo.setInstalledPath(installedPath);

    String appBackupDir = Paths.get(backupDir, "applications", application.getCode()).toString();
    Files.createDirectories(Paths.get(appBackupDir));

    // 备份conf目录
    File confDir = new File(appDir, "conf");
    if (confDir.exists() && confDir.isDirectory()) {
      String confBackupPath = Paths.get(appBackupDir, "conf").toString();
      List<FileMetadata> confMetadata = backupHelper.copyDirectoryWithMetadata(
          confDir.toPath(), Paths.get(confBackupPath), appDir.toPath(), backupType, backup);
      appInfo.getFileMetadataIndex().addAll(confMetadata);
      appInfo.setConfBackedUp(true);
      appInfo.setConfPath("applications/" + application.getCode() + "/conf");
    }

    // 备份data目录
    File dataDir = new File(appDir, "data");
    if (dataDir.exists() && dataDir.isDirectory()) {
      if (backupType == BackupType.FULL) {
        // 全量备份：备份整个data目录
        String dataBackupPath = Paths.get(appBackupDir, "data").toString();
        List<FileMetadata> dataMetadata = backupHelper.copyDirectoryWithMetadata(
            dataDir.toPath(), Paths.get(dataBackupPath), appDir.toPath(), backupType, backup);
        appInfo.getFileMetadataIndex().addAll(dataMetadata);
        appInfo.setDataBackedUp(true);
        appInfo.setDataPath("applications/" + application.getCode() + "/data");
      } else {
        // 增量备份：只备份新增或修改的文件（基于ID判断）
        String dataBackupPath = Paths.get(appBackupDir, "data").toString();
        List<FileMetadata> dataMetadata = backupHelper.backupDataDirectoryIncremental(
            dataDir.toPath(), Paths.get(dataBackupPath), appDir.toPath(), backup, this);
        appInfo.getFileMetadataIndex().addAll(dataMetadata);
        appInfo.setDataBackedUp(true);
        appInfo.setDataPath("applications/" + application.getCode() + "/data");
      }
    }

    // 备份logs目录（仅当 backupLogs 为 true 时）
    if (Boolean.TRUE.equals(backup.getBackupLogs())) {
      File logsDir = new File(appDir, "logs");
      if (logsDir.exists() && logsDir.isDirectory()) {
        String logsBackupPath = Paths.get(appBackupDir, "logs").toString();
        List<FileMetadata> logsMetadata = backupHelper.copyDirectoryWithMetadata(
            logsDir.toPath(), Paths.get(logsBackupPath), appDir.toPath(), backupType, backup);
        appInfo.getFileMetadataIndex().addAll(logsMetadata);
        appInfo.setLogsBackedUp(true);
        appInfo.setLogsPath("applications/" + application.getCode() + "/logs");
      }
    }

    return appInfo;
  }

  /**
   * 保存清单文件
   */
  private void saveManifest(String backupDir, BackupManifest manifest) throws Exception {
    String manifestFile = Paths.get(backupDir, "manifest.json").toString();
    objectMapper.writeValue(new File(manifestFile), manifest);
    log.info("备份清单已保存：{}", manifestFile);
  }
}
