package cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_VERIFICATION_CODE_LENGTH;

import cloud.xcan.angus.api.enums.SignInType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Data
@Accessors(chain = true)
@Schema(description = "找回密码重置密码请求参数")
public class PasswordResetDto {

  @NotNull
  @Schema(description = "验证类型", requiredMode = Schema.RequiredMode.REQUIRED,
      allowableValues = {"SMS_CODE", "EMAIL_CODE"})
  private SignInType type;

  @Schema(description = "用户ID（可选），当相同邮箱/手机号存在多个租户用户时，指定用户ID选择对应找回密码用户")
  private Long userId;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "账号（手机号或邮箱）", requiredMode = Schema.RequiredMode.REQUIRED)
  private String account;

  @NotBlank
  @Length(max = MAX_VERIFICATION_CODE_LENGTH)
  @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String code;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "验证码Key，用于短信验证码和邮件验证码找回密码时链接密码，用于对验证码找回密码安全增强",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String codeKey;

  @NotEmpty
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String newPassword;

  @NotEmpty
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "确认密码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String confirmPassword;

}
