package cloud.xcan.angus.core.repo.interfaces.system.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "认证设置更新请求参数")
public class AuthSettingsUpdateDto {

  @Schema(description = "是否启用LDAP")
  private Boolean ldapEnabled;

  @Schema(description = "LDAP配置（JSON）")
  private String ldapConfig;

  @Schema(description = "是否启用SAML")
  private Boolean samlEnabled;

  @Schema(description = "SAML配置（JSON）")
  private String samlConfig;

  @Schema(description = "密码策略配置（JSON）")
  private String passwordPolicy;

  @Schema(description = "会话超时（分钟）")
  private Integer sessionTimeout;
}
