package cloud.xcan.angus.api.gm.user.vo;

import cloud.xcan.angus.api.commonlink.user.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "用户状态更新响应")
public class UserStatusUpdateVo {

  @Schema(description = "用户ID")
  private Long id;

  @Schema(description = "用户状态")
  private UserStatus status;

  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;
}
