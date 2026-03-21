package cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_EMAIL_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_MOBILE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_VERIFICATION_CODE_LENGTH;

import cloud.xcan.angus.api.enums.SignInType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Data
@Accessors(chain = true)
@Schema(description = "注册请求参数")
public class UserSignupDto {

  @NotNull
  @Schema(description = "注册类型", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {
      "SMS_CODE", "EMAIL_CODE"})
  private SignInType registerType;

  @Length(max = MAX_MOBILE_LENGTH)
  @Schema(description = "手机号（短信注册时必填）")
  private String phone;

  @Email
  @Length(max = MAX_EMAIL_LENGTH)
  @Schema(description = "邮箱（邮箱注册时必填）")
  private String email;

  @NotBlank
  @Length(max = MAX_VERIFICATION_CODE_LENGTH)
  @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String code;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "验证码Key，用于短信验证码和邮件验证码注册时链接密码，用于对验证码注册安全增强", requiredMode = Schema.RequiredMode.REQUIRED)
  private String codeKey;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "姓名")
  private String name;

  @NotEmpty
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;

  @NotEmpty
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "确认密码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String confirmPassword;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "邀请码")
  private String inviteCode;

  @NotNull
  @Schema(description = "同意协议", requiredMode = Schema.RequiredMode.REQUIRED)
  private Boolean agreement;

}
