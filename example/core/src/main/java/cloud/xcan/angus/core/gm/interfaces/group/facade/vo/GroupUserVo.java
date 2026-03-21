package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "组成员VO")
public class GroupUserVo extends TenantAuditingVo {

  @Schema(description = "用户ID")
  private Long id;

  @Schema(description = "用户姓名")
  private String name;

  @Schema(description = "邮箱")
  private String email;

  @Schema(description = "头像")
  private String avatar;

  @Schema(description = "部门")
  @NameJoinField(id = "departmentId", repository = "commonDepartmentRepo")
  private String department;

  @Schema(description = "部门ID")
  private Long departmentId;

  @Schema(description = "加入时间")
  private LocalDateTime joinDate;

  @Schema(description = "是否负责人")
  private Boolean isOwner;
}
