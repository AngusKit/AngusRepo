package cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.role.RoleInfo;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "授权详情VO")
public class AuthorizationDetailVo extends TenantAuditingVo {

  @Schema(description = "授权ID")
  private Long id;

  @Schema(description = "授权主体类型")
  private AuthorizationSubjectType subjectType;

  @Schema(description = "授权主体ID")
  private Long subjectId;

  @Schema(description = "授权主体名称")
  private String subjectName;

  @Schema(description = "授权主体头像（用户）")
  private String subjectAvatar;

  @Schema(description = "授权主体部门（用户）")
  private String subjectDepartment;

  @Schema(description = "授权主体邮箱（用户）")
  private String subjectEmail;

  @Schema(description = "上级部门（部门）")
  private String subjectParent;

  @Schema(description = "授权主体用户数量")
  private Integer subjectUserCount;

  @Schema(description = "授权主体描述")
  private String subjectDescription;

  @Schema(description = "启用状态")
  private EnabledStatus status;

  @Schema(description = "是否开通授权，true：通过开通或私有化安装自动授权（不允许删除），false：手动授权（允许删除）")
  private Boolean opened;

  @Schema(description = "授权开始有效性时间")
  private LocalDateTime validFrom;

  @Schema(description = "授权有效性结束时间")
  private LocalDateTime validTo;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "授权描述")
  private String description;

  @Schema(description = "角色列表")
  private List<RoleInfo> roles;

}
