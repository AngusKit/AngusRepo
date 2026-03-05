package cloud.xcan.angus.core.repo.interfaces.access.facade.dto;

import cloud.xcan.angus.core.repo.domain.access.AccessPrincipalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新访问规则请求参数")
public class AccessRuleUpdateDto {

  @Size(max = 255)
  @Schema(description = "规则名称")
  private String name;

  @Size(max = 2000)
  @Schema(description = "规则描述")
  private String description;

  @Schema(description = "主体类型")
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
