package cloud.xcan.angus.core.gm.interfaces.security.facade.vo;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.remote.vo.AuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "密码策略VO")
public class PasswordPolicyVo extends AuditingVo {

  @Schema(description = "ID", example = "1")
  private Long id;

  @Schema(description = "最小长度", defaultValue = "6", example = "6")
  private Integer minLength;

  @Schema(description = "最大长度", defaultValue = "20", example = "20")
  private Integer maxLength;

  @Schema(description = "要求大写字母", defaultValue = "true", example = "true")
  private Boolean requireUppercase;

  @Schema(description = "要求小写字母", defaultValue = "true", example = "true")
  private Boolean requireLowercase;

  @Schema(description = "要求数字", defaultValue = "true", example = "true")
  private Boolean requireNumbers;

  @Schema(description = "要求特殊字符", defaultValue = "false", example = "false")
  private Boolean requireSpecialChars;

  @Schema(description = "防止重用（最近N个密码）", defaultValue = "6", example = "6")
  private Integer preventReuse;

  @Schema(description = "过期天数，0表示不过期", defaultValue = "0", example = "0")
  private Integer expirationDays;

  @Schema(description = "警告天数，0表示不提示", defaultValue = "0", example = "0")
  private Integer warningDays;

  @Schema(description = "最大登录尝试次数", defaultValue = "6", example = "6")
  private Integer maxLoginAttempts;

  @Schema(description = "锁定时长（分钟）", defaultValue = "30", example = "30")
  private Integer lockoutDuration;

  @Schema(description = "是否启用", example = "true")
  private EnabledStatus status;

}
