package cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X2;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Data
@Accessors(chain = true)
@Schema(description = "刷新Token请求参数")
public class RefreshTokenDto {

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "OAuth2客户端标识符", example = "xcan_tp", requiredMode = Schema.RequiredMode.REQUIRED)
  private String clientId = "xcan_tp";

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "OAuth2客户端密钥", example = "6917ae827c964acc8dd7638fe0581b67", requiredMode = Schema.RequiredMode.REQUIRED)
  private String clientSecret = "6917ae827c964acc8dd7638fe0581b67";

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "刷新Token", requiredMode = Schema.RequiredMode.REQUIRED)
  private String refreshToken;
}
