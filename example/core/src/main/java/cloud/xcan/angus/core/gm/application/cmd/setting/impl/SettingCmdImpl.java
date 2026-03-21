package cloud.xcan.angus.core.gm.application.cmd.setting.impl;

import static cloud.xcan.angus.api.commonlink.setting.Setting.getDefaultAlertRuleSettings;
import static cloud.xcan.angus.api.commonlink.setting.Setting.getDefaultBackupSettings;
import static cloud.xcan.angus.api.commonlink.setting.Setting.getDefaultEurekaConfig;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.SettingRepo;
import cloud.xcan.angus.api.commonlink.setting.alert.AlertRuleSettings;
import cloud.xcan.angus.api.commonlink.setting.backup.BackupSettings;
import cloud.xcan.angus.api.commonlink.setting.eureka.EurekaConfig;
import cloud.xcan.angus.api.commonlink.setting.model.AlertRulesValue;
import cloud.xcan.angus.api.commonlink.setting.model.BackupSettingsValue;
import cloud.xcan.angus.api.commonlink.setting.model.EurekaConfigValue;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.PermissionCheck;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingCmd;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.ProtocolException;
import jakarta.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingCmdImpl extends CommCmd<Setting, Long> implements SettingCmd {

  @Resource
  private SettingRepo settingRepo;

  @Resource
  private SettingManager settingManager;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public BackupSettings update(BackupSettings backupSettings) {
    return new BizTemplate<BackupSettings>() {
      BackupSettings existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();

        // 验证异地同步配置
        if (Boolean.TRUE.equals(backupSettings.getEnableRemoteSync())
            && backupSettings.getRemoteSyncConfig() == null) {
          throw new IllegalArgumentException("启用异地备份同步时，必须配置异地同步配置");
        }
      }

      @Override
      protected BackupSettings process() {
        // 查询或创建备份设置
        Setting setting = settingManager.getSetting0(SettingKey.BACKUP_SETTINGS);
        if (setting != null) {
          existing = setting.getBackupSettings();
        }
        if (existing == null) {
          existing = getDefaultBackupSettings();
        }

        // 存储路径变更且新路径不存在时，初始化存储路径
        String newStoragePath = backupSettings.getStoragePath();
        String oldStoragePath = existing.getStoragePath();
        if (newStoragePath != null && !newStoragePath.trim().isEmpty()
            && !Objects.equals(oldStoragePath, newStoragePath)) {
          Path path = Paths.get(newStoragePath.trim());
          if (!Files.exists(path)) {
            try {
              Files.createDirectories(path);
            } catch (Exception e) {
              throw ProtocolException.of("无法创建备份存储路径: {0}", new Object[]{newStoragePath});
            }
          }
        }

        // 更新字段
        existing.setStoragePath(newStoragePath);
        existing.setMaxStorageSize(backupSettings.getMaxStorageSize());
        existing.setRetentionDays(backupSettings.getRetentionDays());
        existing.setCompressionLevel(backupSettings.getCompressionLevel());
        existing.setVerifyDiskSpace(backupSettings.getVerifyDiskSpace());
        existing.setSendNotification(backupSettings.getSendNotification());
        existing.setEnableRemoteSync(backupSettings.getEnableRemoteSync());
        existing.setRemoteSyncConfig(backupSettings.getRemoteSyncConfig());

        // 保存
        BackupSettingsValue value = new BackupSettingsValue();
        value.setBackupSettings(existing);
        Setting savedSetting;
        if (setting != null) {
          setting.setValue(value);
          savedSetting = settingRepo.save(setting);
        } else {
          Setting newSetting = new Setting();
          newSetting.setId(uidGenerator.getUID());
          newSetting.setKey(SettingKey.BACKUP_SETTINGS);
          newSetting.setValue(value);
          newSetting.setGlobalDefault(true);
          savedSetting = insert(newSetting);
        }

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            savedSetting.getId(),
            savedSetting.getKey().name(),
            OperationMessage.SETTING_UPDATE_BACKUP_DETAILS,
            new Object[]{savedSetting.getKey().name()}
        );

        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AlertRuleSettings update(AlertRuleSettings alertRuleSettings) {
    return new BizTemplate<AlertRuleSettings>() {
      AlertRuleSettings existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();
      }

      @Override
      protected AlertRuleSettings process() {
        // 查询或创建告警规则设置
        Setting setting = settingManager.getSetting0(SettingKey.ALERT_RULES);
        if (setting != null) {
          existing = setting.getAlertRules();
        }
        if (existing == null) {
          existing = getDefaultAlertRuleSettings();
        }

        // 更新字段
        existing.setRules(alertRuleSettings.getRules());

        // 保存
        AlertRulesValue value = new AlertRulesValue();
        value.setAlertRules(existing);
        Setting savedSetting;
        if (setting != null) {
          setting.setValue(value);
          savedSetting = settingRepo.save(setting);
        } else {
          Setting newSetting = new Setting();
          newSetting.setId(uidGenerator.getUID());
          newSetting.setKey(SettingKey.ALERT_RULES);
          newSetting.setValue(value);
          newSetting.setGlobalDefault(true);
          savedSetting = insert(newSetting);
        }

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            savedSetting.getId(),
            savedSetting.getKey().name(),
            OperationMessage.SETTING_UPDATE_ALERT_RULES_DETAILS,
            new Object[]{savedSetting.getKey().name()}
        );

        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public EurekaConfig update(EurekaConfig eurekaConfig) {
    return new BizTemplate<EurekaConfig>() {
      EurekaConfig existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();
      }

      @Override
      protected EurekaConfig process() {
        // 查询或创建Eureka配置
        Setting setting = settingManager.getSetting0(SettingKey.EUREKA_CONFIG);
        if (setting != null) {
          existing = setting.getEurekaConfig();
        }
        if (existing == null) {
          existing = getDefaultEurekaConfig();
        }

        // 更新字段
        existing.setServiceUrl(eurekaConfig.getServiceUrl());
        existing.setEnableAuth(eurekaConfig.getEnableAuth());
        existing.setUsername(eurekaConfig.getUsername());
        existing.setPassword(eurekaConfig.getPassword());
        existing.setSyncInterval(eurekaConfig.getSyncInterval());
        existing.setEnableSsl(eurekaConfig.getEnableSsl());
        existing.setConnectTimeout(eurekaConfig.getConnectTimeout());
        existing.setReadTimeout(eurekaConfig.getReadTimeout());

        // 保存
        EurekaConfigValue value = new EurekaConfigValue();
        value.setEurekaConfig(existing);
        Setting savedSetting;
        if (setting != null) {
          setting.setValue(value);
          savedSetting = settingRepo.save(setting);
        } else {
          Setting newSetting = new Setting();
          newSetting.setId(uidGenerator.getUID());
          newSetting.setKey(SettingKey.EUREKA_CONFIG);
          newSetting.setValue(value);
          newSetting.setGlobalDefault(true);
          savedSetting = insert(newSetting);
        }

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            savedSetting.getId(),
            savedSetting.getKey().name(),
            OperationMessage.SETTING_UPDATE_EUREKA_CONFIG_DETAILS,
            new Object[]{savedSetting.getKey().name()}
        );

        return existing;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Setting, Long> getRepository() {
    return settingRepo;
  }
}
