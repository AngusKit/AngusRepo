package cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto;

import cloud.xcan.angus.core.repo.domain.cleanup.CleanupType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新清理策略请求参数")
public class CleanupPolicyUpdateDto implements Serializable {

  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "策略名称")
  private String name;

  @Size(max = 1000)
  @Schema(description = "策略描述")
  private String description;

  @Schema(description = "仓库ID")
  private String repositoryId;

  @Schema(description = "清理类型")
  private CleanupType type;

  @Schema(description = "是否启用")
  private Boolean enabled;

  @Schema(description = "是否试运行")
  private Boolean dryRun;

  @Schema(description = "清理条件JSON")
  private String conditionJson;

  @Schema(description = "调度配置JSON")
  private String scheduleJson;
}
