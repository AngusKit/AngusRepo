package cloud.xcan.angus.core.repo.interfaces.system.facade.dto;

import cloud.xcan.angus.core.repo.domain.system.StorageBackend;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "存储设置更新请求参数")
public class StorageSettingsUpdateDto implements Serializable {

  @Schema(description = "存储后端类型")
  private StorageBackend backend;

  @Schema(description = "本地存储路径")
  private String localPath;

  @Schema(description = "S3配置（JSON）")
  private String s3Config;

  @Schema(description = "Azure配置（JSON）")
  private String azureConfig;

  @Schema(description = "GCS配置（JSON）")
  private String gcsConfig;
}
