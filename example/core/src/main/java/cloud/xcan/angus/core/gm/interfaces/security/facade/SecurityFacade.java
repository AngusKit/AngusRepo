package cloud.xcan.angus.core.gm.interfaces.security.facade;

import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.IpWhitelistCreateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.IpWhitelistUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.LoginSecurityConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.PasswordPolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityAuditStatsFindDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityNotificationConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.IpWhitelistVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.LoginSecurityConfigVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.PasswordPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityAuditStatsVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityNotificationConfigVo;
import java.util.List;

public interface SecurityFacade {

  PasswordPolicyVo getPasswordPolicy();

  PasswordPolicyVo updatePasswordPolicy(PasswordPolicyUpdateDto dto);

  LoginSecurityConfigVo getLoginSecurityConfig();

  LoginSecurityConfigVo updateLoginSecurityConfig(LoginSecurityConfigUpdateDto dto);

  List<IpWhitelistVo> listIpWhitelist();

  IpWhitelistVo addIpWhitelist(IpWhitelistCreateDto dto);

  IpWhitelistVo updateIpWhitelist(Long id, IpWhitelistUpdateDto dto);

  void deleteIpWhitelist(Long id);

  SecurityAuditStatsVo getAuditStats(SecurityAuditStatsFindDto dto);

  SecurityNotificationConfigVo getNotificationConfig();

  SecurityNotificationConfigVo updateNotificationConfig(SecurityNotificationConfigUpdateDto dto);
}
