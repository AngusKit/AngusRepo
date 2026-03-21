package cloud.xcan.angus.core.gm.interfaces.backup.facade;

import cloud.xcan.angus.core.gm.infra.utils.DownloadResult;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupFindDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupListVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupStatsVo;
import cloud.xcan.angus.remote.PageResult;
import java.io.IOException;

public interface BackupFacade {

  BackupDetailVo createBackup(BackupCreateDto dto);

  void deleteBackup(Long id);

  void runBackup(Long id);

  BackupDetailVo getBackupDetail(Long id);

  DownloadResult downloadBackup(Long id) throws IOException;

  PageResult<BackupListVo> listRecords(BackupFindDto dto);

  BackupStatsVo getStats();
}
