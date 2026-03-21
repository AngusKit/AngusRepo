package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_EMAIL_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Data
@Accessors(chain = true)
@Schema(description = "注册请求参数")
public class UserAcceptInviteDto {

  @NotEmpty
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "邀请码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String inviteCode;

  @NotEmpty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "被邀请人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Email
  @NotEmpty
  @Length(max = MAX_EMAIL_LENGTH)
  @Schema(description = "邮箱（链接邀请时必填）", requiredMode = Schema.RequiredMode.REQUIRED)
  private String email;

  @NotEmpty
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;
  @NotEmpty
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "确认密码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String confirmPassword;

}
