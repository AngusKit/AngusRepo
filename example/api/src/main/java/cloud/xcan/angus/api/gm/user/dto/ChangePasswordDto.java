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
@Schema(description = "修改密码请求参数")
public class ChangePasswordDto {

  @NotEmpty
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "原密码", requiredMode = RequiredMode.REQUIRED)
  private String oldPassword;

  @NotEmpty
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "新密码", requiredMode = RequiredMode.REQUIRED)
  private String newPassword;

  @NotEmpty
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "确认密码", requiredMode = RequiredMode.REQUIRED)
  private String confirmPassword;
}
