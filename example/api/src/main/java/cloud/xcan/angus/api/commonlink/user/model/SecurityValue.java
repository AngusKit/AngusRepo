package cloud.xcan.angus.api.commonlink.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 安全设置值 注意：这里只存储用户的安全设置偏好，不包含安全信息（如最后登录时间等）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "安全设置值")
public class SecurityValue extends UserSettingValue {

  @Schema(description = "双因素认证是否启用")
  private Boolean twoFactorEnabled;

  @Schema(description = "双因素认证密钥")
  private String twoFactorSecret;

  @Schema(description = "备用恢复码列表")
  private List<String> backupCodes;
}
