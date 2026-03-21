package cloud.xcan.angus.api.commonlink.setting.model;

import cloud.xcan.angus.api.commonlink.setting.backup.BackupSettings;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 备份设置值
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "备份设置值")
public class BackupSettingsValue extends SettingValue {

  @Schema(description = "备份设置数据")
  private BackupSettings backupSettings;
}
