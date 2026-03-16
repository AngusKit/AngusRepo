package cloud.xcan.angus.core.repo.interfaces.access.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "权限检查请求参数")
public class PermissionCheckDto implements Serializable {

  @NotBlank
  @Schema(description = "权限类型", requiredMode = Schema.RequiredMode.REQUIRED)
  private String permission;

  @Schema(description = "资源路径")
  private String path;
}
