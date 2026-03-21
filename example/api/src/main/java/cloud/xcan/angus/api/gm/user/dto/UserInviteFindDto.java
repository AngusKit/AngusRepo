package cloud.xcan.angus.api.gm.user.dto;

import cloud.xcan.angus.api.commonlink.user.enums.InviteStatus;
import cloud.xcan.angus.api.commonlink.user.enums.InviteType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询邀请列表请求参数")
public class UserInviteFindDto extends PageQuery {

  @Schema(description = "邀请应用ID")
  private Long appId;

  @Schema(description = "邀请邮箱")
  private String email;

  @Schema(description = "邀请方式")
  private InviteType inviteType;

  @Schema(description = "状态筛选（待接受、已过期、已接受、已取消）")
  private InviteStatus status;
}
