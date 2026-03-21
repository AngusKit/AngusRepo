package cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo;

import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "用户信息")
public class UserInfoVo extends TenantAuditingVo {

  @Schema(description = "用户ID")
  private String id;

  @Schema(description = "用户名")
  private String username;

  @Schema(description = "姓名")
  private String name;

  @Schema(description = "邮箱")
  private String email;

  @Schema(description = "手机号")
  private String phone;

  @Schema(description = "头像")
  private String avatar;

  @Schema(description = "账号类型")
  private String accountType;

  @Schema(description = "角色列表")
  private List<RoleInfoVo> roles;

  @Schema(description = "权限列表")
  private List<String> permissions;
}
