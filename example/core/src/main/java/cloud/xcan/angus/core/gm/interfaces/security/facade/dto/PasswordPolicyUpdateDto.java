package cloud.xcan.angus.core.gm.interfaces.security.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "密码策略更新DTO")
public class PasswordPolicyUpdateDto {

  @Min(6)
  @Max(20)
  @Schema(description = "最小长度，支持范围6-20，默认6", example = "10")
  private Integer minLength = 6;

  @Min(6)
  @Max(20)
  @Schema(description = "最大长度，固定值20", example = "20", accessMode = AccessMode.READ_ONLY)
  private final Integer maxLength = 20;

  @Schema(description = "是否要求大写字母，默认开启", example = "true")
  private Boolean requireUppercase = true;

  @Schema(description = "是否要求小写字母，默认开启", example = "true")
  private Boolean requireLowercase = true;

  @Schema(description = "是否要求数字，默认开启", example = "true")
  private Boolean requireNumbers = true;

  @Schema(description = "是否要求特殊字符，默认关闭", example = "false")
  private Boolean requireSpecialChars = false;

  @Min(0)
  @Max(100)
  @Schema(description = "禁止重复使用次数，默认6次", example = "6")
  private Integer preventReuse = 6;

  @Min(0)
  @Max(3650)
  @Schema(description = "密码过期天数，默认0不过期", example = "60")
  private Integer expirationDays = 0;

  @Min(0)
  @Max(365)
  @Schema(description = "提前警告天数，默认0不提示", example = "7")
  private Integer warningDays;

  @Min(1)
  @Max(100)
  @Schema(description = "最大登录尝试次数，默认6次", example = "6")
  private Integer maxLoginAttempts = 6;

  @Min(1)
  @Max(1440)
  @Schema(description = "锁定时长（分钟），默认30分钟", example = "60")
  private Integer lockoutDuration = 30;
}
