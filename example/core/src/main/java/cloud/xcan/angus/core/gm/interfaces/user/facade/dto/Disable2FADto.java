package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "禁用双因素认证请求参数")
public class Disable2FADto {

  @NotBlank
  @Schema(description = "当前密码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;

  @NotBlank
  @Length(min = 6, max = 6)
  @Schema(description = "6位验证码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String code;
}
