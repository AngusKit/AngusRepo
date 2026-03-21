package cloud.xcan.angus.core.gm.interfaces.department.facade.dto;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询部门请求参数")
public class DepartmentFindDto extends PageQuery {

  @Schema(description = "部门ID")
  private Long id;

  @Schema(description = "部门名称")
  private String name;

  @Schema(description = "部门编码")
  private String code;

  @Schema(description = "父部门ID")
  private Long parentId;

  @Schema(description = "负责人ID")
  private Long leaderId;

  @Schema(description = "状态筛选")
  private EnabledStatus status;

  @Schema(description = "层级筛选")
  private Integer level;

}
