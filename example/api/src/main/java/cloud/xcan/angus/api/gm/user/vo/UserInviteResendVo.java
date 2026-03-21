package cloud.xcan.angus.api.gm.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "重新发送邀请响应")
public class UserInviteResendVo {

  @Schema(description = "邀请ID")
  private Long id;

  @Schema(description = "重新发送时间")
  private LocalDateTime resentTime;
}
