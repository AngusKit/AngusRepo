package cloud.xcan.angus.core.gm.application.cmd.security;

import cloud.xcan.angus.core.gm.domain.security.Security;
import cloud.xcan.angus.core.gm.domain.security.model.IpWhitelistConfig;
import cloud.xcan.angus.core.gm.domain.security.model.LoginSecurityConfig;
import cloud.xcan.angus.core.gm.domain.security.model.PasswordPolicyConfig;
import cloud.xcan.angus.core.gm.domain.security.model.SecurityNotificationConfig;

public interface SecurityCmd {

  Security updatePasswordPolicy(PasswordPolicyConfig config);

  Security updateLoginSecurityConfig(LoginSecurityConfig config);

  Security addIpWhitelist(IpWhitelistConfig config);

  Security updateIpWhitelist(Long id, IpWhitelistConfig config);

  void deleteIpWhitelist(Long id);

  Security updateNotificationConfig(SecurityNotificationConfig config);
}
