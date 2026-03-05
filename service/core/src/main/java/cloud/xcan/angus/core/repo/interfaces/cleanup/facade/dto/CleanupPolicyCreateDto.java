package cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto;

import cloud.xcan.angus.core.repo.domain.cleanup.CleanupType;
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
@Schema(description = "创建清理策略请求参数")
public class CleanupPolicyCreateDto {

  @NotBlank
  @Size(max = 255)
  @Schema(description = "策略名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Size(max = 1000)
  @Schema(description = "策略描述")
  private String description;

  @NotBlank
  @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED)
  private String repositoryId;

  @NotNull
  @Schema(description = "清理类型", requiredMode = Schema.RequiredMode.REQUIRED)
  private CleanupType type;

  @Schema(description = "是否启用")
  private Boolean enabled = true;

  @Schema(description = "是否试运行")
  private Boolean dryRun = false;

  @Schema(description = "清理条件JSON")
  private String conditionJson;

  @Schema(description = "调度配置JSON")
  private String scheduleJson;
}
