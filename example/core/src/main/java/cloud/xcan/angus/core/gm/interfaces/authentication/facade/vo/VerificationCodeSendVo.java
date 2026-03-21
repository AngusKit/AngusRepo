package cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "验证码发送响应")
public class VerificationCodeSendVo {

  @Schema(description = "验证码Key")
  private String codeKey;

  @Schema(description = "过期时间（秒）")
  private Integer expireTime;
}
