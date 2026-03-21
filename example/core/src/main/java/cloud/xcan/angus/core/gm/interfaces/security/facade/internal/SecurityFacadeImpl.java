package cloud.xcan.angus.core.gm.interfaces.security.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.security.SecurityCmd;
import cloud.xcan.angus.core.gm.application.query.security.SecurityQuery;
import cloud.xcan.angus.core.gm.domain.security.Security;
import cloud.xcan.angus.core.gm.domain.security.SecurityRepo;
import cloud.xcan.angus.core.gm.domain.security.model.IpWhitelistConfig;
import cloud.xcan.angus.core.gm.domain.security.model.LoginSecurityConfig;
import cloud.xcan.angus.core.gm.domain.security.model.PasswordPolicyConfig;
import cloud.xcan.angus.core.gm.domain.security.model.SecurityNotificationConfig;
import cloud.xcan.angus.core.gm.interfaces.security.facade.SecurityFacade;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.IpWhitelistCreateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.IpWhitelistUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.LoginSecurityConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.PasswordPolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityAuditStatsFindDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityNotificationConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.internal.assembler.SecurityAssembler;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.IpWhitelistVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.LoginSecurityConfigVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.PasswordPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityAuditStatsVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityNotificationConfigVo;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SecurityFacadeImpl implements SecurityFacade {

  @Resource
  private SecurityCmd securityCmd;

  @Resource
  private SecurityQuery securityQuery;

  @Resource
  private SecurityRepo securityRepo;

  @Override
  public PasswordPolicyVo getPasswordPolicy() {
    return SecurityAssembler.toPasswordPolicyVo(securityQuery.getPasswordPolicy());
  }

  @Override
  public PasswordPolicyVo updatePasswordPolicy(PasswordPolicyUpdateDto dto) {
    Security existingSecurity = securityQuery.getPasswordPolicy();
    PasswordPolicyConfig existingConfig = existingSecurity != null
        && existingSecurity.getConfig() instanceof PasswordPolicyConfig
        ? (PasswordPolicyConfig) existingSecurity.getConfig() : null;
    PasswordPolicyConfig config = SecurityAssembler.toPasswordPolicyConfig(dto, existingConfig);
    Security saved = securityCmd.updatePasswordPolicy(config);
    return SecurityAssembler.toPasswordPolicyVo(saved);
  }

  @Override
  public LoginSecurityConfigVo getLoginSecurityConfig() {
    return SecurityAssembler.toLoginSecurityConfigVo(securityQuery.getLoginSecurityConfig());
  }

  @Override
  public LoginSecurityConfigVo updateLoginSecurityConfig(LoginSecurityConfigUpdateDto dto) {
    Security existingSecurity = securityQuery.getLoginSecurityConfig();
    LoginSecurityConfig existingConfig = existingSecurity != null
        && existingSecurity.getConfig() instanceof LoginSecurityConfig
        ? (LoginSecurityConfig) existingSecurity.getConfig()
        : null;
    LoginSecurityConfig config = SecurityAssembler.toTwoFactorAuthConfig(dto, existingConfig);
    Security saved = securityCmd.updateLoginSecurityConfig(config);
    return SecurityAssembler.toLoginSecurityConfigVo(saved);
  }

  @Override
  public List<IpWhitelistVo> listIpWhitelist() {
    return SecurityAssembler.toIpWhitelistVoList(securityQuery.listIpWhitelist());
  }

  @Override
  public IpWhitelistVo addIpWhitelist(IpWhitelistCreateDto dto) {
    IpWhitelistConfig config = SecurityAssembler.toIpWhitelistConfig(dto);
    Security saved = securityCmd.addIpWhitelist(config);
    return SecurityAssembler.toIpWhitelistVo(saved);
  }

  @Override
  public IpWhitelistVo updateIpWhitelist(Long id, IpWhitelistUpdateDto dto) {
    Security existingSecurity = securityRepo.findById(id).orElse(null);
    IpWhitelistConfig existingConfig = existingSecurity != null
        && existingSecurity.getConfig() instanceof IpWhitelistConfig
        ? (IpWhitelistConfig) existingSecurity.getConfig()
        : null;
    IpWhitelistConfig config = SecurityAssembler.toIpWhitelistConfig(dto, existingConfig);
    Security saved = securityCmd.updateIpWhitelist(id, config);
    return SecurityAssembler.toIpWhitelistVo(saved);
  }

  @Override
  public void deleteIpWhitelist(Long id) {
    securityCmd.deleteIpWhitelist(id);
  }

  @Override
  public SecurityAuditStatsVo getAuditStats(SecurityAuditStatsFindDto dto) {
    return securityQuery.getAuditStats(dto.getStartDate(), dto.getEndDate());
  }

  @Override
  public SecurityNotificationConfigVo getNotificationConfig() {
    return SecurityAssembler.toNotificationConfigVo(securityQuery.getNotificationConfig());
  }

  @Override
  public SecurityNotificationConfigVo updateNotificationConfig(
      SecurityNotificationConfigUpdateDto dto) {
    Security existingSecurity = securityQuery.getNotificationConfig();
    SecurityNotificationConfig existingConfig = existingSecurity != null
        && existingSecurity.getConfig() instanceof SecurityNotificationConfig
        ? (SecurityNotificationConfig) existingSecurity.getConfig()
        : null;
    SecurityNotificationConfig config = SecurityAssembler.toNotificationConfig(dto, existingConfig);
    Security saved = securityCmd.updateNotificationConfig(config);
    return SecurityAssembler.toNotificationConfigVo(saved);
  }
}
