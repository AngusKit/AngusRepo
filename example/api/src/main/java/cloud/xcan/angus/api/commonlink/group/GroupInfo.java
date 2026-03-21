package cloud.xcan.angus.api.commonlink.group;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.group.enums.GroupType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
@Schema(description = "组信息")
public class GroupInfo {

  @Schema(description = "组ID")
  private Long id;

  @Schema(description = "组名称")
  private String name;

  @Schema(description = "组编码")
  private String code;

  @Schema(description = "组描述")
  private String description;

  @Schema(description = "组类型")
  private GroupType type;

  @Schema(description = "启用状态")
  private EnabledStatus status;

  @Schema(description = "所有者ID")
  private Long ownerId;
}
