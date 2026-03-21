package cloud.xcan.angus.core.gm.application.cmd.setting;

import cloud.xcan.angus.api.commonlink.setting.alert.AlertRuleSettings;
import cloud.xcan.angus.api.commonlink.setting.backup.BackupSettings;
import cloud.xcan.angus.api.commonlink.setting.eureka.EurekaConfig;

public interface SettingCmd {

  BackupSettings update(BackupSettings backupSettings);

  AlertRuleSettings update(AlertRuleSettings alertRuleSettings);

  EurekaConfig update(EurekaConfig eurekaConfig);
}
