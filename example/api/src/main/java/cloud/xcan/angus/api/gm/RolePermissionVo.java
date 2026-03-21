package cloud.xcan.angus.api.gm;

import cloud.xcan.angus.api.commonlink.role.PermissionInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "角色权限配置")
public class RolePermissionVo {

  @Schema(description = "角色ID")
  private Long roleId;

  @Schema(description = "角色名称")
  private String roleName;

  @Schema(description = "权限列表")
  private List<PermissionInfo> permissions;

  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;

}
