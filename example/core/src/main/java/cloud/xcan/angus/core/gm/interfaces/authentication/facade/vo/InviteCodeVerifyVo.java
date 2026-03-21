package cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "邀请码验证响应")
public class InviteCodeVerifyVo {

  @Schema(description = "是否有效")
  private Boolean valid;

  @Schema(description = "过期日期")
  private LocalDateTime expireDate;
}
