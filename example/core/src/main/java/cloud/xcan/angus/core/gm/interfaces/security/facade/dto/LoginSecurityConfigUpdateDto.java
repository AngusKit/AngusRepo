package cloud.xcan.angus.core.gm.interfaces.security.facade.dto;

import cloud.xcan.angus.api.enums.SignInType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "登录安全配置更新DTO")
public class LoginSecurityConfigUpdateDto {

  @Min(1)
  @Max(10)
  @Schema(description = "最大登录尝试次数", example = "5")
  private Integer maxLoginAttempts;

  @Min(1)
  @Max(1440)
  @Schema(description = "账户锁定时长(分钟)", example = "30")
  private Integer accountLockoutDurationMinutes;

  @Min(1)
  @Max(14400)
  @Schema(description = "会话超时时间(分钟)，默认3天，最大10天", example = "4320")
  private Integer sessionTimeoutMinutes;

  @Schema(description = "是否启用图形验证码", example = "true")
  private Boolean graphicalCaptchaEnabled;

  @NotEmpty
  @Size(min = 1, max = 4)
  @Schema(description = "支持的登录类型列表", requiredMode = RequiredMode.REQUIRED)
  private List<SignInType> signInTypes;

  @Schema(description = "默认登录类型", example = "SMS_CODE")
  private SignInType defaultSignInType;

  @Min(60)
  @Max(86400)
  @Schema(description = "验证码过期时间（秒），默认5分钟", example = "600")
  private Integer codeExpiration = 300;

  @Min(0)
  @Max(365)
  @Schema(description = "信任设备天数", example = "30")
  private Integer trustDeviceDays = 30;

  @Schema(description = "是否启用双因素认证", example = "true")
  private Boolean twoFactorEnabled;

  @Schema(description = "双因素认证方式", example = "EMAIL_CODE")
  private SignInType twoFactorAuthMethod;

  @Schema(description = "是否强制管理员使用双因素认证", example = "true")
  private Boolean enforceTwoFactorForAdmins = false;

  @Schema(description = "是否强制所有用户使用双因素认证", example = "true")
  private Boolean enforceTwoFactorForAllUsers = false;

  @Schema(description = "是否允许注册新账号（仅云服务版和数据中心版可开启）", example = "false")
  private Boolean allowRegistrationEnabled;
}
