package cloud.xcan.angus.core.repo.interfaces.system.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "存储设置信息")
public class StorageSettingsVo {

  @Schema(description = "存储后端类型")
  private String backend;

  @Schema(description = "本地存储路径")
  private String localPath;

  @Schema(description = "S3配置")
  private String s3Config;

  @Schema(description = "Azure配置")
  private String azureConfig;

  @Schema(description = "GCS配置")
  private String gcsConfig;
}
