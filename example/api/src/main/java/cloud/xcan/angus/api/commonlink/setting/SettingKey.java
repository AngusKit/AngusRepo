package cloud.xcan.angus.api.commonlink.setting;

import cloud.xcan.angus.spec.experimental.Value;
import lombok.Getter;


@Getter
public enum SettingKey implements Value<String> {
  LOCALE,
  SOCIAL,
  BACKUP_SETTINGS,
  LOG_RETENTION_CONFIGS,
  EUREKA_CONFIG,
  ALERT_RULES;

  @Override
  public String getValue() {
    return this.name();
  }
}
