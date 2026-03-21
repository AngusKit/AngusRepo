package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.group.enums.GroupType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询组请求参数")
public class GroupFindDto extends PageQuery {

  @Schema(description = "组ID")
  private Long id;

  @Schema(description = "组名称")
  private String name;

  @Schema(description = "状态筛选")
  private EnabledStatus status;

  @Schema(description = "类型筛选")
  private GroupType type;

  @Schema(description = "负责人ID")
  private Long ownerId;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
