package cloud.xcan.angus.core.repo.interfaces.format.facade.dto;

import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "格式制品上传请求参数")
public class FormatArtifactUploadDto implements Serializable {

  @NotNull
  @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long repositoryId;

  @NotBlank
  @Size(max = 500)
  @Schema(description = "制品名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Size(max = MAX_NAME_LENGTH)
  @Schema(description = "制品版本")
  private String version;

  @Schema(description = "制品描述")
  private String description;

  @Schema(description = "格式特定元数据（JSON）")
  private String metadata;
}
