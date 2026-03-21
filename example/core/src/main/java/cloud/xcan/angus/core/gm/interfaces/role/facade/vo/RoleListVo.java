package cloud.xcan.angus.core.gm.interfaces.role.facade.vo;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色列表项")
public class RoleListVo extends TenantAuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "角色名称")
  private String name;

  @Schema(description = "角色编码")
  private String code;

  @Schema(description = "描述")
  private String description;

  @Schema(description = "角色状态")
  private EnabledStatus status;

  @Schema(description = "是否系统角色")
  private Boolean isSystem;

  @Schema(description = "是否默认角色")
  private Boolean isDefault;

  @Schema(description = "用户数量")
  private Long userCount;

  @Schema(description = "应用ID")
  private Long appId;

  @Schema(description = "应用名称")
  @NameJoinField(id = "appId", repository = "applicationRepo")
  private String appName;
}
