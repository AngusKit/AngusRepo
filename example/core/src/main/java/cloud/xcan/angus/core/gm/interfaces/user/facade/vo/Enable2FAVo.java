package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "启用双因素认证响应")
public class Enable2FAVo {

  @Schema(description = "二维码图片（Base64）")
  private String qrCode;

  @Schema(description = "密钥")
  private String secret;

  @Schema(description = "备用恢复码列表")
  private List<String> backupCodes;
}
