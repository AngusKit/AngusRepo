package cloud.xcan.angus.core.repo.interfaces.access.facade.dto;

import cloud.xcan.angus.core.repo.domain.access.AccessPrincipalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;
import static cloud.xcan.angus.core.repo.domain.Constants.*;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新访问规则请求参数")
public class AccessRuleUpdateDto implements Serializable {

  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "规则名称")
  private String name;

  @Length(max = MAX_CONTENT_LENGTH)
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
