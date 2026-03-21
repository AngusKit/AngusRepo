package cloud.xcan.angus.core.gm.interfaces.backup.facade;

import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupSettingsUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupSettingsVo;

public interface BackupSettingsFacade {

  /**
   * <p>更新备份设置</p>
   */
  BackupSettingsVo update(BackupSettingsUpdateDto dto);

  /**
   * <p>查询备份设置</p>
   */
  BackupSettingsVo getSettings();
}
