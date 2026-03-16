package cloud.xcan.angus.core.repo.interfaces.system.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "认证设置信息")
public class AuthSettingsVo implements Serializable {

  @Schema(description = "是否启用LDAP")
  private Boolean ldapEnabled;

  @Schema(description = "LDAP配置")
  private String ldapConfig;

  @Schema(description = "是否启用SAML")
  private Boolean samlEnabled;

  @Schema(description = "SAML配置")
  private String samlConfig;

  @Schema(description = "密码策略")
  private String passwordPolicy;

  @Schema(description = "会话超时（分钟）")
  private Integer sessionTimeout;
}
