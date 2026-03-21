package cloud.xcan.angus.core.gm.interfaces.role.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "更新角色权限请求参数")
public class RolePermissionUpdateDto {

  @NotEmpty
  @Schema(description = "权限列表", requiredMode = RequiredMode.REQUIRED)
  private List<RolePermissionDto> permissions;

}
