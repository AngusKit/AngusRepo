package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "启用双因素认证请求参数")
public class Enable2FADto {

  @NotBlank
  @Schema(description = "当前密码（用于验证）", requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;
}
