package cloud.xcan.angus.api.gm.user.vo;

import cloud.xcan.angus.api.commonlink.user.enums.InviteStatus;
import cloud.xcan.angus.api.commonlink.user.enums.InviteType;
import cloud.xcan.angus.remote.NameJoinField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "用户邀请响应")
public class UserInviteVo {

  @Schema(description = "邀请ID")
  private Long id;

  @Schema(description = "邮箱")
  private String email;

  @Schema(description = "邀请方式")
  private InviteType inviteType;

  @Schema(description = "应用ID")
  private Long appId;

  @Schema(description = "应用名称")
  @NameJoinField(id = "appId", repository = "commonApplicationRepo")
  private String appName;

  @Schema(description = "角色ID")
  private Long roleId;

  @Schema(description = "角色名称")
  @NameJoinField(id = "roleId", repository = "commonRoleRepo")
  private String roleName;

  @Schema(description = "部门ID")
  private Long departmentId;

  @Schema(description = "部门名称")
  @NameJoinField(id = "departmentId", repository = "commonDepartmentRepo")
  private String departmentName;

  @Schema(description = "邀请消息")
  private String message;

  @Schema(description = "邀请人id")
  private Long invitedBy;

  @Schema(description = "邀请人姓名")
  @NameJoinField(id = "invitedBy", repository = "commonUserBaseRepo")
  private String inviterName;

  @Schema(description = "邀请时间")
  private LocalDateTime inviteDate;

  @Schema(description = "过期时间")
  private LocalDateTime expiryDate;

  @Schema(description = "状态")
  private InviteStatus status;

  @Schema(description = "邀请码")
  private String inviteCode;

  @Schema(description = "邀请链接")
  private String inviteUrl;

  @Schema(description = "租户ID")
  private Long tenantId;

  @Schema(description = "租户名称")
  @NameJoinField(id = "tenantId", repository = "commonTenantRepo")
  private String tenantMame;
}
