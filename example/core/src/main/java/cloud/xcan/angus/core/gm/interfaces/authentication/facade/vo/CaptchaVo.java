package cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "验证码图片响应")
public class CaptchaVo {

  @Schema(description = "验证码Key")
  private String captchaKey;

  @Schema(description = "验证码图片（Base64）")
  private String captchaImage;

  @Schema(description = "过期时间（秒）")
  private Integer expireTime;
}
