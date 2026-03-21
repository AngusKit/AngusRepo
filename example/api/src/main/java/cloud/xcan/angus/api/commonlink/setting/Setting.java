package cloud.xcan.angus.api.commonlink.setting;

import cloud.xcan.angus.api.commonlink.setting.alert.AlertLevel;
import cloud.xcan.angus.api.commonlink.setting.alert.AlertRule;
import cloud.xcan.angus.api.commonlink.setting.alert.AlertRuleSettings;
import cloud.xcan.angus.api.commonlink.setting.backup.BackupSettings;
import cloud.xcan.angus.api.commonlink.setting.backup.CompressionLevel;
import cloud.xcan.angus.api.commonlink.setting.eureka.EurekaConfig;
import cloud.xcan.angus.api.commonlink.setting.locale.Locale;
import cloud.xcan.angus.api.commonlink.setting.logretention.LogRetentionConfig;
import cloud.xcan.angus.api.commonlink.setting.model.AlertRulesValue;
import cloud.xcan.angus.api.commonlink.setting.model.BackupSettingsValue;
import cloud.xcan.angus.api.commonlink.setting.model.EurekaConfigValue;
import cloud.xcan.angus.api.commonlink.setting.model.LocaleValue;
import cloud.xcan.angus.api.commonlink.setting.model.LogRetentionConfigsValue;
import cloud.xcan.angus.api.commonlink.setting.model.SettingValue;
import cloud.xcan.angus.api.commonlink.setting.model.SocialValue;
import cloud.xcan.angus.api.commonlink.setting.social.Social;
import cloud.xcan.angus.core.utils.SpringAppDirUtils;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "gm_setting")
@Setter
@Getter
@Accessors(chain = true)
public class Setting extends EntitySupport<Setting, Long> implements Serializable {

  @Id
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "`key`")
  private SettingKey key;

  @Column(name = "value", columnDefinition = "json")
  @Type(JsonType.class)
  private SettingValue value;

  @Column(name = "global_default")
  private Boolean globalDefault;

  @Transient
  @JsonIgnore
  public Locale getLocale() {
    if (value instanceof LocaleValue localeValue) {
      return localeValue.getLocale();
    }
    return null;
  }

  @Transient
  @JsonIgnore
  public Social getSocial() {
    if (value instanceof SocialValue socialValue) {
      return socialValue.getSocial();
    }
    return null;
  }

  @Transient
  @JsonIgnore
  public BackupSettings getBackupSettings() {
    if (value instanceof BackupSettingsValue backupSettingsValue) {
      return backupSettingsValue.getBackupSettings();
    }
    return null;
  }

  @Transient
  @JsonIgnore
  public List<LogRetentionConfig> getLogRetentionConfigs() {
    if (value instanceof LogRetentionConfigsValue logRetentionConfigsValue) {
      return logRetentionConfigsValue.getLogRetentionConfigs();
    }
    return null;
  }

  @Transient
  @JsonIgnore
  public EurekaConfig getEurekaConfig() {
    if (value instanceof EurekaConfigValue eurekaConfigValue) {
      return eurekaConfigValue.getEurekaConfig();
    }
    return null;
  }

  @Transient
  @JsonIgnore
  public AlertRuleSettings getAlertRules() {
    if (value instanceof AlertRulesValue alertRulesValue) {
      return alertRulesValue.getAlertRules();
    }
    return null;
  }

  @Transient
  @JsonIgnore
  public static BackupSettings getDefaultBackupSettings() {
    BackupSettings defaultSettings = new BackupSettings();
    defaultSettings.setStoragePath(new SpringAppDirUtils().getHomeDir() + "backups");
    defaultSettings.setMaxStorageSize(100);
    defaultSettings.setRetentionDays(90);
    defaultSettings.setCompressionLevel(CompressionLevel.STANDARD);
    defaultSettings.setVerifyDiskSpace(true);
    defaultSettings.setSendNotification(true);
    defaultSettings.setEnableRemoteSync(false);
    defaultSettings.setRemoteSyncConfig(null);
    return defaultSettings;
  }

  @Transient
  @JsonIgnore
  public static AlertRuleSettings getDefaultAlertRuleSettings() {
    AlertRuleSettings defaultSettings = new AlertRuleSettings();
    List<AlertRule> defaultRules = new ArrayList<>();

    // CPU使用率告警规则
    AlertRule cpuRule = new AlertRule();
    cpuRule.setName("CPU使用率告警");
    cpuRule.setMetric("cpu_usage");
    cpuRule.setCondition(">");
    cpuRule.setThreshold(80.0);
    cpuRule.setDuration(300); // 5分钟
    cpuRule.setLevel(AlertLevel.HIGH);
    defaultRules.add(cpuRule);

    // 内存使用率告警规则
    AlertRule memoryRule = new AlertRule();
    memoryRule.setName("内存使用率告警");
    memoryRule.setMetric("memory_usage");
    memoryRule.setCondition(">");
    memoryRule.setThreshold(85.0);
    memoryRule.setDuration(300); // 5分钟
    memoryRule.setLevel(AlertLevel.HIGH);
    defaultRules.add(memoryRule);

    // 磁盘使用率告警规则
    AlertRule diskRule = new AlertRule();
    diskRule.setName("磁盘使用率告警");
    diskRule.setMetric("disk_usage");
    diskRule.setCondition(">");
    diskRule.setThreshold(90.0);
    diskRule.setDuration(600); // 10分钟
    diskRule.setLevel(AlertLevel.HIGH);
    defaultRules.add(diskRule);

    defaultSettings.setRules(defaultRules);
    return defaultSettings;
  }

  @Transient
  @JsonIgnore
  public static LogRetentionConfig getDefaultLogRetentionConfig() {
    LogRetentionConfig defaultSettings = new LogRetentionConfig();
    defaultSettings.setUserLogRetentionDays(90);
    defaultSettings.setSystemLogRetentionDays(60);
    defaultSettings.setApiLogRetentionDays(30);
    return defaultSettings;
  }

  @Transient
  @JsonIgnore
  public static EurekaConfig getDefaultEurekaConfig() {
    return new EurekaConfig()
        .setServiceUrl("http://localhost:1806/eureka/")
        .setEnableAuth(false)
        .setSyncInterval(30)
        .setEnableSsl(false)
        .setConnectTimeout(5000)
        .setReadTimeout(8000);
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
