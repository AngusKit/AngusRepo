package cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler;

import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.formatFileSize;

import cloud.xcan.angus.api.commonlink.setting.backup.BackupSettings;
import cloud.xcan.angus.api.commonlink.setting.backup.RemoteSyncConfig;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupSettingsUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.RemoteSyncConfigDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupSettingsVo;

public class BackupSettingsAssembler {

  public static BackupSettings toUpdateDomain(BackupSettingsUpdateDto dto) {
    BackupSettings settings = new BackupSettings();
    settings.setStoragePath(dto.getStoragePath());
    settings.setMaxStorageSize(dto.getMaxStorageSize());
    settings.setRetentionDays(dto.getRetentionDays());
    settings.setCompressionLevel(dto.getCompressionLevel());
    settings.setVerifyDiskSpace(dto.getVerifyDiskSpace());
    settings.setSendNotification(dto.getSendNotification());
    settings.setEnableRemoteSync(dto.getEnableRemoteSync());

    // 转换异地同步配置
    if (dto.getRemoteSyncConfig() != null) {
      settings.setRemoteSyncConfig(toRemoteSyncConfig(dto.getRemoteSyncConfig()));
    }
    return settings;
  }

  public static BackupSettingsVo toVo(BackupSettings settings, Long usedStorageSize) {
    BackupSettingsVo vo = new BackupSettingsVo();
    vo.setStoragePath(settings.getStoragePath());
    vo.setMaxStorageSize(settings.getMaxStorageSize());
    vo.setUsedStorageSize(formatFileSize(usedStorageSize));
    vo.setRetentionDays(settings.getRetentionDays());
    vo.setCompressionLevel(settings.getCompressionLevel());
    vo.setVerifyDiskSpace(settings.getVerifyDiskSpace());
    vo.setSendNotification(settings.getSendNotification());
    vo.setEnableRemoteSync(settings.getEnableRemoteSync());
    vo.setRemoteSyncConfig(settings.getRemoteSyncConfig());
    return vo;
  }

  private static RemoteSyncConfig toRemoteSyncConfig(RemoteSyncConfigDto dto) {
    RemoteSyncConfig config = new RemoteSyncConfig();
    config.setSyncType(dto.getSyncType());
    config.setHost(dto.getHost());
    config.setPort(dto.getPort());
    config.setUsername(dto.getUsername());
    config.setPassword(dto.getPassword());
    config.setRemotePath(dto.getRemotePath());
    return config;
  }
}
