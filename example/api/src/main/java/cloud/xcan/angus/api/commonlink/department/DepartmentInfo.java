package cloud.xcan.angus.api.commonlink.department;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
@Schema(description = "部门信息")
public class DepartmentInfo {

  @Schema(description = "部门ID")
  private Long id;

  @Schema(description = "部门名称")
  private String name;

  @Schema(description = "部门编码")
  private String code;

  @Schema(description = "部门描述")
  private String description;

  @Schema(description = "启用状态")
  private EnabledStatus status;

  @Schema(description = "父部门ID")
  private Long parentId;

  @Schema(description = "部门层级")
  private Integer level;

  @Schema(description = "排序顺序")
  private Integer sortOrder;

  @Schema(description = "负责人ID")
  private Long leaderId;


}
