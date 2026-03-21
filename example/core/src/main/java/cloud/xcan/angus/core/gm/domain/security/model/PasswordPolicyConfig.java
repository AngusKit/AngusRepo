package cloud.xcan.angus.core.gm.domain.security.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "密码策略配置")
public class PasswordPolicyConfig extends SecurityConfig {

  @Schema(description = "最小长度", example = "6")
  private Integer minLength = 6;

  @Schema(description = "最大长度", example = "20")
  private Integer maxLength = 20;

  @Schema(description = "要求大写字母", example = "true")
  private Boolean requireUppercase = true;

  @Schema(description = "要求小写字母", example = "true")
  private Boolean requireLowercase = true;

  @Schema(description = "要求数字", example = "true")
  private Boolean requireNumbers = true;

  @Schema(description = "要求特殊字符", example = "false")
  private Boolean requireSpecialChars = false;

  @Schema(description = "防止重用（最近N个密码）", example = "6")
  private Integer preventReuse = 6;

  @Schema(description = "过期天数", example = "0")
  private Integer expirationDays = 0;

  @Schema(description = "警告天数", example = "0")
  private Integer warningDays = 0;

  @Schema(description = "最大登录尝试次数", example = "6")
  private Integer maxLoginAttempts = 6;

  @Schema(description = "锁定时长（分钟）", example = "30")
  private Integer lockoutDuration = 30;
}
