package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import cloud.xcan.angus.api.commonlink.setting.backup.CompressionLevel;
import cloud.xcan.angus.api.commonlink.setting.backup.RemoteSyncConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "备份设置详情")
public class BackupSettingsVo {

  @Schema(description = "备份存储路径")
  private String storagePath;

  @Schema(description = "最大存储空间(GB)")
  private Integer maxStorageSize;

  @Schema(description = "已使用存储空间")
  private String usedStorageSize;

  @Schema(description = "备份文件保留天数")
  private Integer retentionDays;

  @Schema(description = "备份压缩级别")
  private CompressionLevel compressionLevel;

  @Schema(description = "备份前验证磁盘空间")
  private Boolean verifyDiskSpace;

  @Schema(description = "备份完成后发送通知")
  private Boolean sendNotification;

  @Schema(description = "启用异地备份同步")
  private Boolean enableRemoteSync;

  @Schema(description = "异地同步配置")
  private RemoteSyncConfig remoteSyncConfig;
}
