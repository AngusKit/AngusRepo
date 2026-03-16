package cloud.xcan.angus.core.repo.interfaces.team.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "接受邀请结果")
public class InvitationAcceptResultVo implements Serializable {

  @Schema(description = "是否成功")
  private Boolean success;

  @Schema(description = "消息")
  private String message;

  @Schema(description = "成员信息")
  private TeamMemberVo member;
}
