package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import cloud.xcan.angus.core.gm.domain.user.enums.PasswordStrength;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 用户安全设置详情VO
 */
@Data
@Schema(description = "用户安全设置详情")
public class UserSecurityVo {

  @Schema(description = "用户ID")
  private Long userId;

  @Schema(description = "双因素认证是否启用")
  private Boolean twoFactorEnabled;

  @Schema(description = "密码最后修改时间")
  private LocalDateTime passwordLastChanged;

  @Schema(description = "密码强度")
  private PasswordStrength passwordStrength;

  @Schema(description = "是否有备用恢复码")
  private Boolean hasBackupCodes;

  @Schema(description = "剩余备用恢复码数量")
  private Integer backupCodesRemaining;

  @Schema(description = "最后登录时间")
  private LocalDateTime lastLoginAt;

  @Schema(description = "最后登录IP")
  private String lastLoginIp;

  @Schema(description = "最后登录地点")
  private String lastLoginLocation;

  @Schema(description = "最后登录设备")
  private String lastLoginDevice;
}
