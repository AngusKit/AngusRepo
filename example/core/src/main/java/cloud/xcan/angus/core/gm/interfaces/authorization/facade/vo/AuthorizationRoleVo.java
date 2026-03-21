package cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo;

import cloud.xcan.angus.api.commonlink.role.RoleInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "添加角色结果")
public class AuthorizationRoleVo {

  @Schema(description = "授权ID")
  private Long authorizationId;

  @Schema(description = "添加或删除角色列表")
  private List<RoleInfo> roles;

  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;

}
