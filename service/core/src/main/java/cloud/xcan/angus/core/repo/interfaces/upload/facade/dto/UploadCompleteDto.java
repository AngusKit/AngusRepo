package cloud.xcan.angus.core.repo.interfaces.upload.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "完成上传任务请求参数")
public class UploadCompleteDto {

  @Size(max = 2000)
  @Schema(description = "制品描述")
  private String description;

  @Size(max = 255)
  @Schema(description = "许可证")
  private String license;

  @Size(max = 500)
  @Schema(description = "标签（逗号分隔）")
  private String tags;

  @Size(max = 4000)
  @Schema(description = "元数据（JSON格式）")
  private String metadata;
}
