package cloud.xcan.angus.core.repo.interfaces.repository.facade.dto;

import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryType;
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
@Schema(description = "创建仓库请求参数")
public class RepositoryCreateDto {

  @NotBlank
  @Size(max = 255)
  @Schema(description = "仓库名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @NotNull
  @Schema(description = "仓库格式", requiredMode = Schema.RequiredMode.REQUIRED)
  private RepositoryFormat format;

  @NotNull
  @Schema(description = "仓库类型", requiredMode = Schema.RequiredMode.REQUIRED)
  private RepositoryType type;

  @Size(max = 2000)
  @Schema(description = "仓库描述")
  private String description;

  @Schema(description = "远程仓库URL（代理类型必填）")
  private String remoteUrl;

  @Schema(description = "Blob存储")
  private String blobStore;

  @Schema(description = "仓库设置（JSON）")
  private String settings;
}
