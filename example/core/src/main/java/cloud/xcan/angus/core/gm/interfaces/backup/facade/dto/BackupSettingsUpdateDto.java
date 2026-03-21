package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_FILE_PATH;

import cloud.xcan.angus.api.commonlink.setting.backup.CompressionLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "更新备份设置请求参数")
public class BackupSettingsUpdateDto {

  @NotBlank
  @Length(max = MAX_FILE_PATH)
  @Schema(description = "备份存储路径", requiredMode = Schema.RequiredMode.REQUIRED)
  private String storagePath;

  @NotNull
  @Min(1)
  @Max(10000)
  @Schema(description = "最大存储空间(GB)", requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer maxStorageSize;

  @NotNull
  @Min(1)
  @Max(3650)
  @Schema(description = "备份文件保留天数", requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer retentionDays;

  @NotNull
  @Schema(description = "备份压缩级别", requiredMode = Schema.RequiredMode.REQUIRED)
  private CompressionLevel compressionLevel;

  @NotNull
  @Schema(description = "备份前验证磁盘空间", requiredMode = Schema.RequiredMode.REQUIRED)
  private Boolean verifyDiskSpace;

  @NotNull
  @Schema(description = "备份完成后发送通知", requiredMode = Schema.RequiredMode.REQUIRED)
  private Boolean sendNotification;

  @NotNull
  @Schema(description = "启用异地备份同步", requiredMode = Schema.RequiredMode.REQUIRED)
  private Boolean enableRemoteSync;

  // TODO 后期支持
  @Valid
  @Schema(description = "异地同步配置(enableRemoteSync=true时必填)")
  private RemoteSyncConfigDto remoteSyncConfig;
}
