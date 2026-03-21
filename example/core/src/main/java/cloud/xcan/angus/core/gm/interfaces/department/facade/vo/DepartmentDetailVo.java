package cloud.xcan.angus.core.gm.interfaces.department.facade.vo;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "部门详情")
public class DepartmentDetailVo extends TenantAuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "部门名称")
  private String name;

  @Schema(description = "部门编码")
  private String code;

  @Schema(description = "父部门ID")
  private Long parentId;

  @Schema(description = "父部门名称")
  private String parentName;

  @Schema(description = "层级")
  private Integer level;

  @Schema(description = "排序")
  private Integer sortOrder;

  @Schema(description = "负责人ID")
  private Long leaderId;

  @Schema(description = "负责人姓名")
  private String leaderName;

  @Schema(description = "负责人头像")
  private String leaderAvatar;

  @Schema(description = "描述")
  private String description;

  @Schema(description = "状态")
  private EnabledStatus status;

  @Schema(description = "用户数量")
  private Long userCount;

  @Schema(description = "部门路径")
  private String path;

  @Schema(description = "用户列表")
  private List<DepartmentUserVo> users;

  @Schema(description = "子部门列表（树形结构）")
  private List<DepartmentDetailVo> children;

  @Data
  @Schema(description = "子部门信息")
  public static class DepartmentSubDepartmentVo {

    @Schema(description = "部门ID")
    private Long id;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "用户数量")
    private Long userCount;
  }
}
