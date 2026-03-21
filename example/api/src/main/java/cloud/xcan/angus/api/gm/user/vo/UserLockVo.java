package cloud.xcan.angus.api.gm.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "用户锁定响应")
public class UserLockVo {

  @Schema(description = "用户ID")
  private Long id;

  @Schema(description = "是否锁定")
  private Boolean locked;

  @Schema(description = "锁定原因")
  private String lockReason;

  @Schema(description = "锁定时间")
  private LocalDateTime lockTime;
}
