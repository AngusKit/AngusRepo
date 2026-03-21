package cloud.xcan.angus.api.gm.user.dto;

import cloud.xcan.angus.api.commonlink.user.enums.UserStatus;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户查询DTO")
public class UserFindDto extends PageQuery {

  @Schema(description = "用户ID")
  private Long id;

  @Schema(description = "用户名称")
  private String name;

  @Schema(description = "邮箱")
  private String email;

  @Schema(description = "手机号")
  private String phone;

  @Schema(description = "状态")
  private UserStatus status;

  @Schema(description = "角色ID")
  private Long roleId;

  @Schema(description = "部门ID")
  private Long departmentId;

  @Schema(description = "部门ID")
  private Long groupId;

  @Schema(description = "锁定状态")
  private Boolean locked;

  @Schema(description = "在线状态")
  private Boolean online;

  @Schema(description = "应用编码，用于筛选拥有该应用权限的用户")
  private String appCode;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
