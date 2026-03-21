package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.group.enums.GroupType;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "组详情")
public class GroupDetailVo extends TenantAuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "组名称")
  private String name;

  @Schema(description = "组编码")
  private String code;

  @Schema(description = "描述")
  private String description;

  @Schema(description = "组类型")
  private GroupType type;

  @Schema(description = "状态")
  private EnabledStatus status;

  @Schema(description = "负责人ID")
  private Long ownerId;

  @Schema(description = "负责人姓名")
  private String ownerName;

  @Schema(description = "负责人头像")
  private String ownerAvatar;

  @Schema(description = "最后活跃时间")
  private LocalDateTime lastActive;

  @Schema(description = "成员数量")
  private Long userCount;

  @Schema(description = "标签列表")
  private List<String> tags;

}
