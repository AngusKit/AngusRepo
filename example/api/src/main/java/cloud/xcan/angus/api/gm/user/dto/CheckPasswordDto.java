package cloud.xcan.angus.api.gm.user.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Data
@Accessors(chain = true)
@Schema(description = "检查用户密码请求参数")
public class CheckPasswordDto {

  @NotEmpty
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "待校验的密码", requiredMode = RequiredMode.REQUIRED)
  private String password;
}
