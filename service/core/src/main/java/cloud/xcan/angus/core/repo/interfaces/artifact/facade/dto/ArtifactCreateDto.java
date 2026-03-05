package cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "创建制品请求参数")
public class ArtifactCreateDto {

  @NotNull
  @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long repositoryId;

  @NotBlank
  @Size(max = 255)
  @Schema(description = "制品名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Schema(description = "制品路径")
  private String path;

  @Size(max = 255)
  @Schema(description = "制品版本")
  private String version;

  @Size(max = 2000)
  @Schema(description = "制品描述")
  private String description;

  @Schema(description = "文件大小（字节）")
  private Long sizeBytes;

  @Size(max = 255)
  @Schema(description = "校验和")
  private String checksum;

  @Size(max = 255)
  @Schema(description = "许可证")
  private String license;

  @Schema(description = "标签（JSON）")
  private String tags;

  @Schema(description = "元数据（JSON）")
  private String metadata;
}
