package cloud.xcan.angus.core.repo.interfaces.access.facade.dto;

import cloud.xcan.angus.core.repo.domain.access.AccessPrincipalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;
import static cloud.xcan.angus.spec.experimental.BizConstant.*;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "创建访问规则请求参数")
public class AccessRuleCreateDto implements Serializable {

  @NotBlank
  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Size(max = 2000)
  @Schema(description = "规则描述")
  private String description;

  @NotNull
  @Schema(description = "主体类型", requiredMode = Schema.RequiredMode.REQUIRED)
  private AccessPrincipalType principalType;

  @Schema(description = "主体ID")
  private String principalId;

  @Schema(description = "权限列表（JSON）")
  private String permissions;

  @Schema(description = "路径列表（JSON）")
  private String paths;

  @Schema(description = "是否启用")
  private Boolean enabled;

  @Schema(description = "优先级")
  private Integer priority;

  @Schema(description = "过期时间")
  private LocalDateTime expiresAt;
}
