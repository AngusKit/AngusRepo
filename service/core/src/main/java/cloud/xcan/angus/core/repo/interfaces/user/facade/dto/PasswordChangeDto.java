package cloud.xcan.angus.core.repo.interfaces.user.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "修改密码请求参数")
public class PasswordChangeDto implements Serializable {

  @NotBlank
  @Schema(description = "当前密码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String currentPassword;

  @NotBlank
  @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String newPassword;

  @NotBlank
  @Schema(description = "确认新密码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String confirmPassword;
}
