package cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.USER_TOKEN_CLIENT_SCOPE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X5;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_VERIFICATION_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.XCAN_TENANT_PLATFORM_CODE;

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
@Schema(description = "登录请求参数")
public class UserSignInDto {

  @NotNull
  @Schema(description = "登录类型", requiredMode = Schema.RequiredMode.REQUIRED)
  private SignInType loginType;

  @Schema(description = "用户ID（可选），当相同邮箱/手机号存在多个租户用户时，指定用户ID选择对应登录用户")
  private Long userId;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "OAuth2客户端标识符", example = "xcan_tp", requiredMode = Schema.RequiredMode.REQUIRED)
  private String clientId = XCAN_TENANT_PLATFORM_CODE;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "OAuth2客户端密钥", example = "6917ae827c964acc8dd7638fe0581b67", requiredMode = Schema.RequiredMode.REQUIRED)
  private String clientSecret = "6917ae827c964acc8dd7638fe0581b67";

  @NotEmpty
  @Length(max = MAX_CODE_LENGTH_X5)
  @Schema(description = "OAuth2作用域，指定资源访问的授权权限。多个值用逗号分隔",
      requiredMode = Schema.RequiredMode.REQUIRED, example = "user_trust")
  private String scope = USER_TOKEN_CLIENT_SCOPE;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "用户登录账号（用户名、手机号或邮箱地址，根据登录类型必填）", requiredMode = Schema.RequiredMode.REQUIRED)
  private String account;

  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "密码（账号密码登录时必填）")
  private String password;

  @Length(max = MAX_VERIFICATION_CODE_LENGTH)
  @Schema(description = "验证码（短信/邮箱验证码登录时必填）")
  private String code;

  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "验证码Key，用于短信验证码和邮件验证码登录时链接密码，用于对验证码登录安全增强")
  private String codeKey;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "图形验证码（账号密码登录时可选）")
  private String captcha;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "图形验证码Key（账号密码登录时可选）")
  private String captchaKey;
}
