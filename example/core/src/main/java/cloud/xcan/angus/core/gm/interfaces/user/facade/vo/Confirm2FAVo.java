package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "确认启用双因素认证响应")
public class Confirm2FAVo {

  @Schema(description = "用户ID")
  private Long userId;

  @Schema(description = "双因素认证是否启用")
  private Boolean twoFactorEnabled;

  @Schema(description = "启用时间")
  private LocalDateTime enabledAt;
}
