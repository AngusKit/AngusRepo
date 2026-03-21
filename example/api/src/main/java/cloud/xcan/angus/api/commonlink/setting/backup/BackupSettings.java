package cloud.xcan.angus.api.commonlink.setting.backup;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BackupSettings {

  /**
   * 备份存储路径
   */
  private String storagePath;

  /**
   * 最大存储空间(GB)
   */
  private Integer maxStorageSize;

  /**
   * 备份文件保留天数
   */
  private Integer retentionDays;

  /**
   * 备份压缩级别
   */
  private CompressionLevel compressionLevel;

  /**
   * 备份前验证磁盘空间
   */
  private Boolean verifyDiskSpace = true;

  /**
   * 备份完成后发送通知
   */
  private Boolean sendNotification = true;

  /**
   * 启用异地备份同步
   */
  private Boolean enableRemoteSync = false;

  /**
   * 异地同步配置
   */
  private RemoteSyncConfig remoteSyncConfig;

}
