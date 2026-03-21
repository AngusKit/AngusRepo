package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.group.enums.GroupType;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户所在的组列表VO")
public class GroupOwnerVo extends TenantAuditingVo {

  @Schema(description = "组ID")
  private Long id;

  @Schema(description = "组名称")
  private String name;

  @Schema(description = "组编码")
  private String code;

  @Schema(description = "组类型")
  private GroupType type;

  @Schema(description = "负责人ID")
  private Long ownerId;

  @Schema(description = "负责人姓名")
  @NameJoinField(id = "ownerId", repository = "commonUserBaseRepo")
  private String ownerName;

  @Schema(description = "负责人头像")
  private String ownerAvatar;

  @Schema(description = "成员数量")
  private Long userCount;

  @Schema(description = "状态")
  private EnabledStatus status;

}
