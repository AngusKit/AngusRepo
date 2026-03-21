package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_FILE_PATH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_HOST_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.commonlink.setting.backup.SyncType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "异地同步配置")
public class RemoteSyncConfigDto {

  @NotNull
  @Schema(description = "同步类型", requiredMode = Schema.RequiredMode.REQUIRED)
  private SyncType syncType;

  @NotBlank
  @Length(max = MAX_HOST_LENGTH)
  @Schema(description = "远程主机地址", requiredMode = Schema.RequiredMode.REQUIRED)
  private String host;

  @NotNull
  @Min(1)
  @Max(65535)
  @Schema(description = "端口号", requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer port;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
  private String username;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "密码(加密存储)", requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;

  @NotBlank
  @Length(max = MAX_FILE_PATH)
  @Schema(description = "远程路径", requiredMode = Schema.RequiredMode.REQUIRED)
  private String remotePath;
}
