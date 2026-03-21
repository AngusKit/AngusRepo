package cloud.xcan.angus.core.gm.application.cmd.log.impl;

import static cloud.xcan.angus.api.commonlink.setting.Setting.getDefaultLogRetentionConfig;

import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.SettingRepo;
import cloud.xcan.angus.api.commonlink.setting.logretention.LogRetentionCleanupResult;
import cloud.xcan.angus.api.commonlink.setting.logretention.LogRetentionConfig;
import cloud.xcan.angus.api.commonlink.setting.model.LogRetentionConfigsValue;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.PermissionCheck;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.LogRetentionConfigCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLogRepo;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.SystemLog;
import cloud.xcan.angus.core.gm.domain.log.SystemLogRepo;
import cloud.xcan.angus.core.gm.domain.log.UserOperationLogRepo;
import cloud.xcan.angus.core.gm.domain.log.enums.LogStatus;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.PrincipalContextUtils;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 日志清理配置命令服务实现
 */
@Slf4j
@Service
public class LogRetentionConfigCmdImpl extends CommCmd<Setting, Long>
    implements LogRetentionConfigCmd {

  @Resource
  private SystemLogRepo systemLogRepo;

  @Resource
  private UserOperationLogRepo userOperationLogRepo;

  @Resource
  private InterfaceRequestLogRepo interfaceRequestLogRepo;

  @Resource
  private SettingRepo settingRepo;

  @Resource
  private SettingManager settingManager;

  @Resource
  private ApplicationQuery applicationQuery;

  @Resource
  private LogRetentionConfigCmd self;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public LogRetentionConfig update(LogRetentionConfig config) {
    return new BizTemplate<LogRetentionConfig>() {
      @Override
      protected LogRetentionConfig process() {
        if (PrincipalContextUtils.isUserAction()) {
          PermissionCheck.checkCloudTenantSecurity();
        }

        // 获取或创建Setting
        Setting setting = settingManager.getSetting0(SettingKey.LOG_RETENTION_CONFIGS);
        List<LogRetentionConfig> configs = new ArrayList<>();

        if (setting != null) {
          configs = setting.getLogRetentionConfigs();
        }

        if (configs == null) {
          List<Application> applications = applicationQuery.findAll();
          configs = applications.stream()
              .map(x -> getDefaultLogRetentionConfig().setApplicationId(x.getId()))
              .collect(Collectors.toList());
        }

        // 查找或创建配置
        LogRetentionConfig existing = configs.stream()
            .filter(c -> config.getApplicationId().equals(c.getApplicationId()))
            .findFirst()
            .orElse(null);

        if (existing == null) {
          existing = new LogRetentionConfig();
          existing.setApplicationId(config.getApplicationId());
          configs.add(existing);
        }

        // 更新配置
        existing.setUserLogRetentionDays(config.getUserLogRetentionDays());
        existing.setSystemLogRetentionDays(config.getSystemLogRetentionDays());
        existing.setApiLogRetentionDays(config.getApiLogRetentionDays());

        // 保存
        LogRetentionConfigsValue value = new LogRetentionConfigsValue();
        value.setLogRetentionConfigs(configs);
        if (setting == null) {
          setting = new Setting();
          setting.setId(uidGenerator.getUID());
          setting.setKey(SettingKey.LOG_RETENTION_CONFIGS);
          setting.setGlobalDefault(true);
        }
        setting.setValue(value);
        settingRepo.save(setting);

        // 记录操作日志
        String appName = "全部应用";
        if (existing.getApplicationId() != null) {
          appName = applicationQuery.findAndCheck(existing.getApplicationId()).getName();
        }
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            existing.getApplicationId(),
            appName,
            OperationMessage.LOG_RETENTION_CONFIG_UPDATE_DETAILS,
            new Object[]{
                appName,
                existing.getUserLogRetentionDays(),
                existing.getSystemLogRetentionDays(),
                existing.getApiLogRetentionDays()
            }
        );

        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<LogRetentionConfig> batchUpdate(List<LogRetentionConfig> configs) {
    return new BizTemplate<List<LogRetentionConfig>>() {
      @Override
      protected List<LogRetentionConfig> process() {
        if (PrincipalContextUtils.isUserAction()) {
          PermissionCheck.checkCloudTenantSecurity();
        }

        // 获取或创建Setting
        Setting setting = settingManager.getSetting0(SettingKey.LOG_RETENTION_CONFIGS);
        List<LogRetentionConfig> existingConfigs = new ArrayList<>();

        if (setting != null) {
          existingConfigs = setting.getLogRetentionConfigs();
        }

        if (existingConfigs == null) {
          List<Application> applications = applicationQuery.findAll();
          existingConfigs = applications.stream()
              .map(x -> getDefaultLogRetentionConfig().setApplicationId(x.getId()))
              .collect(Collectors.toList());
        }

        // 创建应用ID到配置的映射
        Map<Long, LogRetentionConfig> configMap = existingConfigs.stream()
            .collect(Collectors.toMap(LogRetentionConfig::getApplicationId, c -> c));

        // 批量更新或新增
        for (LogRetentionConfig config : configs) {
          LogRetentionConfig existing = configMap.get(config.getApplicationId());
          if (existing == null) {
            existingConfigs.add(config);
          } else {
            existing.setUserLogRetentionDays(config.getUserLogRetentionDays());
            existing.setSystemLogRetentionDays(config.getSystemLogRetentionDays());
            existing.setApiLogRetentionDays(config.getApiLogRetentionDays());
          }
        }

        // 保存
        LogRetentionConfigsValue value = new LogRetentionConfigsValue();
        value.setLogRetentionConfigs(existingConfigs);
        if (setting == null) {
          setting = new Setting();
          setting.setId(uidGenerator.getUID());
          setting.setKey(SettingKey.LOG_RETENTION_CONFIGS);
          setting.setGlobalDefault(true);
        }
        setting.setValue(value);
        settingRepo.save(setting);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            null,
            "日志保留配置",
            OperationMessage.LOG_RETENTION_CONFIG_BATCH_UPDATE_DETAILS,
            new Object[]{existingConfigs.size()}
        );

        return existingConfigs;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public LogRetentionCleanupResult cleanup(Long applicationId, Boolean dryRun) {
    return new BizTemplate<LogRetentionCleanupResult>() {
      @Override
      protected LogRetentionCleanupResult process() {
        if (PrincipalContextUtils.isUserAction()) {
          PermissionCheck.checkCloudTenantSecurity();
        }

        // 获取配置
        Setting setting = settingManager.getSetting0(SettingKey.LOG_RETENTION_CONFIGS);
        List<LogRetentionConfig> configs = new ArrayList<>();

        if (setting != null) {
          configs = setting.getLogRetentionConfigs();
        }

        LogRetentionCleanupResult result = new LogRetentionCleanupResult();
        for (LogRetentionConfig config : configs) {
          self.cleanupLogs(config, result);
        }

        // 记录操作日志
        String appName = "全部应用";
        if (applicationId != null) {
          appName = applicationQuery.findAndCheck(applicationId).getName();
        }
        String dryRunText = Boolean.TRUE.equals(dryRun) ? "YES" : "NO";
        long totalSizeMB =
            result.getTotalSizeFreed() != null ? result.getTotalSizeFreed() / (1024 * 1024) : 0;
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.CONFIG,
            applicationId,
            appName,
            OperationMessage.LOG_RETENTION_CLEANUP_DETAILS,
            new Object[]{
                appName,
                result.getTotalRecordsDeleted() != null ? result.getTotalRecordsDeleted() : 0,
                totalSizeMB,
                dryRunText
            }
        );

        return result;
      }
    }.execute();
  }

  /**
   * 清理指定配置的日志
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void cleanupLogs(LogRetentionConfig config, LogRetentionCleanupResult result) {
    Application application = applicationQuery.findById(config.getApplicationId());
    if (application == null) {
      return;
    }

    String applicationCode = application.getCode();
    LocalDateTime now = LocalDateTime.now();

    // 初始化结果字段
    if (result.getUserLogsDeleted() == null) {
      result.setUserLogsDeleted(0L);
    }
    if (result.getSystemLogsDeleted() == null) {
      result.setSystemLogsDeleted(0L);
    }
    if (result.getApiLogsDeleted() == null) {
      result.setApiLogsDeleted(0L);
    }
    if (result.getTotalRecordsDeleted() == null) {
      result.setTotalRecordsDeleted(0L);
    }
    if (result.getTotalSizeFreed() == null) {
      result.setTotalSizeFreed(0L);
    }

    // 清理用户操作日志
    if (config.getUserLogRetentionDays() > 0) {
      LocalDateTime userLogBeforeDate = now.minusDays(config.getUserLogRetentionDays());
      int deletedUserLogs = userOperationLogRepo.deleteByCreatedDateBefore(
          userLogBeforeDate);
      if (deletedUserLogs > 0) {
        log.info("应用「{}」清理用户操作日志：删除 {} 条记录（保留天数：{}）", applicationCode,
            deletedUserLogs, config.getUserLogRetentionDays());
        result.setUserLogsDeleted(result.getUserLogsDeleted() + deletedUserLogs);
        result.setTotalRecordsDeleted(result.getTotalRecordsDeleted() + deletedUserLogs);
      }
    }

    // 清理系统日志（只清理归档状态的日志，需要先删除物理文件）
    if (config.getSystemLogRetentionDays() > 0) {
      LocalDate systemLogBeforeDate = now.minusDays(config.getSystemLogRetentionDays())
          .toLocalDate();
      List<SystemLog> systemLogsToDelete = systemLogRepo.findByDateBeforeAndStatus(
          systemLogBeforeDate, LogStatus.ARCHIVED, application.getId());

      int deletedFiles = 0;
      long deletedSize = 0;
      for (SystemLog systemLog : systemLogsToDelete) {
        try {
          // 删除物理文件
          Path filePath = Paths.get(systemLog.getFilePath());
          if (Files.exists(filePath)) {
            long fileSize = Files.size(filePath);
            Files.delete(filePath);
            deletedFiles++;
            deletedSize += fileSize;
          }
        } catch (IOException e) {
          String errorMsg = String.format("删除系统日志文件失败：%s - %s",
              systemLog.getFilePath(), e.getMessage());
          log.warn(errorMsg, e);
          result.getErrors().add(errorMsg);
        }
      }

      // 删除数据库记录（只删除归档状态的日志）
      int deletedSystemLogs = systemLogRepo.deleteByDateBeforeAndStatus(systemLogBeforeDate,
          LogStatus.ARCHIVED, application.getId());
      if (deletedSystemLogs > 0) {
        log.info(
            "应用「{}」清理系统日志（归档状态）：删除 {} 条记录，{} 个文件，总大小 {} 字节（保留天数：{}）",
            applicationCode, deletedSystemLogs, deletedFiles, deletedSize,
            config.getSystemLogRetentionDays());
        result.setSystemLogsDeleted(result.getSystemLogsDeleted() + deletedSystemLogs);
        result.setTotalRecordsDeleted(result.getTotalRecordsDeleted() + deletedSystemLogs);
        result.setTotalSizeFreed(result.getTotalSizeFreed() + deletedSize);
      }
    }

    // 清理API请求日志
    if (config.getApiLogRetentionDays() > 0) {
      LocalDateTime apiLogBeforeDate = now.minusDays(config.getApiLogRetentionDays());
      int deletedApiLogs = interfaceRequestLogRepo.deleteByCreatedDateBefore(
          apiLogBeforeDate, application.getCode());
      if (deletedApiLogs > 0) {
        log.info("应用「{}」清理API请求日志：删除 {} 条记录（保留天数：{}）",
            applicationCode, deletedApiLogs, config.getApiLogRetentionDays());
        result.setApiLogsDeleted(result.getApiLogsDeleted() + deletedApiLogs);
        result.setTotalRecordsDeleted(result.getTotalRecordsDeleted() + deletedApiLogs);
      }
    }

    // 更新配置的最后清理时间
    config.setLastCleanupDate(now);
    update(config);
  }

  @Override
  protected BaseRepository<Setting, Long> getRepository() {
    return settingRepo;
  }
}
