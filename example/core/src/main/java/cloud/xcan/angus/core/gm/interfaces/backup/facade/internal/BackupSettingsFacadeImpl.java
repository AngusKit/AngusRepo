package cloud.xcan.angus.core.gm.interfaces.backup.facade.internal;

import cloud.xcan.angus.api.commonlink.setting.backup.BackupSettings;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingCmd;
import cloud.xcan.angus.core.gm.application.query.backup.BackupQuery;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.BackupSettingsFacade;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupSettingsUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler.BackupSettingsAssembler;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupSettingsVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class BackupSettingsFacadeImpl implements BackupSettingsFacade {

  @Resource
  private BackupQuery backupQuery;

  @Resource
  private SettingCmd settingCmd;

  @Override
  public BackupSettingsVo update(BackupSettingsUpdateDto dto) {
    BackupSettings settings = BackupSettingsAssembler.toUpdateDomain(dto);
    BackupSettings saved = settingCmd.update(settings);
    Long usedStorageSize = backupQuery.calculateUsedStorageSize();
    return BackupSettingsAssembler.toVo(saved, usedStorageSize);
  }

  @Override
  public BackupSettingsVo getSettings() {
    BackupSettings backupSettings = backupQuery.getBackupSettings();
    Long usedStorageSize = backupQuery.calculateUsedStorageSize();
    return BackupSettingsAssembler.toVo(backupSettings, usedStorageSize);
  }

}
