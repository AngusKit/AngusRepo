package cloud.xcan.angus.core.gm.domain.security.model;

import cloud.xcan.angus.api.enums.SignInType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "登录安全配置")
public class LoginSecurityConfig extends SecurityConfig {

  @Schema(description = "最大登录尝试次数", example = "5")
  private Integer maxLoginAttempts = 5;

  @Schema(description = "账户锁定时长(分钟)", example = "30")
  private Integer accountLockoutDurationMinutes = 30;

  @Schema(description = "会话超时时间(分钟)，默认3天，最大10天", example = "4320")
  private Integer sessionTimeoutMinutes = 4320;

  @Schema(description = "是否启用图形验证码", example = "true")
  private Boolean graphicalCaptchaEnabled = true;

  @Schema(description = "支持的登录类型列表", example = "[\"SMS_CODE\", \"EMAIL_CODE\"]")
  private List<SignInType> signInTypes = new ArrayList<>(
      List.of(SignInType.ACCOUNT_PASSWORD, SignInType.SMS_CODE,
          SignInType.EMAIL_CODE, SignInType.THIRD_SOCIAL));

  @Schema(description = "默认登录类型", example = "ACCOUNT_PASSWORD")
  private SignInType defaultSignInType = SignInType.ACCOUNT_PASSWORD;

  @Schema(description = "验证码过期时间（秒）", example = "300")
  private Integer codeExpiration = 300;

  @Schema(description = "信任设备天数", example = "30")
  private Integer trustDeviceDays = 30;

  @Schema(description = "是否启用双因素认证", example = "true")
  private Boolean twoFactorEnabled = true;

  @Schema(description = "双因素认证方式", example = "EMAIL_CODE")
  private SignInType twoFactorAuthMethod = SignInType.EMAIL_CODE;

  @Schema(description = "是否强制管理员使用双因素认证", example = "true")
  private Boolean enforceTwoFactorForAdmins = false;

  @Schema(description = "是否强制所有用户使用双因素认证", example = "false")
  private Boolean enforceTwoFactorForAllUsers = false;

  @Schema(description = "是否允许注册新账号（仅云服务版和数据中心版可开启）", example = "false")
  private Boolean allowRegistrationEnabled = false;
}
