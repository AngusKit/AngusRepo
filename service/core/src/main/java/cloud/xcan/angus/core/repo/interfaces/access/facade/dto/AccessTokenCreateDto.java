package cloud.xcan.angus.core.repo.interfaces.access.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "创建访问令牌请求参数")
public class AccessTokenCreateDto implements Serializable {

  @NotBlank
  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "令牌名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Size(max = 2000)
  @Schema(description = "令牌描述")
  private String description;

  @Schema(description = "权限列表（JSON）")
  private String permissions;

  @Schema(description = "过期时间")
  private LocalDateTime expiresAt;

  @Schema(description = "IP白名单（JSON）")
  private String ipWhitelist;
}
