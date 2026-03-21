package cloud.xcan.angus.core.gm.interfaces.security.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.security.Security;
import cloud.xcan.angus.core.gm.domain.security.model.IpWhitelistConfig;
import cloud.xcan.angus.core.gm.domain.security.model.LoginSecurityConfig;
import cloud.xcan.angus.core.gm.domain.security.model.PasswordPolicyConfig;
import cloud.xcan.angus.core.gm.domain.security.model.RecipientUser;
import cloud.xcan.angus.core.gm.domain.security.model.SecurityNotificationConfig;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.IpWhitelistCreateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.IpWhitelistUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.LoginSecurityConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.PasswordPolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.RecipientUserDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityNotificationConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.IpWhitelistVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.LoginSecurityConfigVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.PasswordPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityNotificationConfigVo;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityAssembler {

  public static PasswordPolicyConfig toPasswordPolicyConfig(PasswordPolicyUpdateDto dto,
      PasswordPolicyConfig existingConfig) {
    PasswordPolicyConfig config =
        existingConfig != null ? existingConfig : new PasswordPolicyConfig();

    if (dto.getMinLength() != null) {
      config.setMinLength(dto.getMinLength());
    }
    if (dto.getMaxLength() != null) {
      config.setMaxLength(dto.getMaxLength());
    }
    if (dto.getRequireUppercase() != null) {
      config.setRequireUppercase(dto.getRequireUppercase());
    }
    if (dto.getRequireLowercase() != null) {
      config.setRequireLowercase(dto.getRequireLowercase());
    }
    if (dto.getRequireNumbers() != null) {
      config.setRequireNumbers(dto.getRequireNumbers());
    }
    if (dto.getRequireSpecialChars() != null) {
      config.setRequireSpecialChars(dto.getRequireSpecialChars());
    }
    if (dto.getPreventReuse() != null) {
      config.setPreventReuse(dto.getPreventReuse());
    }
    if (dto.getExpirationDays() != null) {
      config.setExpirationDays(dto.getExpirationDays());
    }
    if (dto.getWarningDays() != null) {
      config.setWarningDays(dto.getWarningDays());
    }
    if (dto.getMaxLoginAttempts() != null) {
      config.setMaxLoginAttempts(dto.getMaxLoginAttempts());
    }
    if (dto.getLockoutDuration() != null) {
      config.setLockoutDuration(dto.getLockoutDuration());
    }
    return config;
  }

  public static PasswordPolicyVo toPasswordPolicyVo(Security security) {
    PasswordPolicyVo vo = new PasswordPolicyVo();
    vo.setId(security.getId());
    vo.setStatus(nullSafe(security.getStatus(), EnabledStatus.ENABLED));

    if (security.getConfig() instanceof PasswordPolicyConfig config) {
      vo.setMinLength(config.getMinLength());
      vo.setMaxLength(config.getMaxLength());
      vo.setRequireUppercase(config.getRequireUppercase());
      vo.setRequireLowercase(config.getRequireLowercase());
      vo.setRequireNumbers(config.getRequireNumbers());
      vo.setRequireSpecialChars(config.getRequireSpecialChars());
      vo.setPreventReuse(config.getPreventReuse());
      vo.setExpirationDays(config.getExpirationDays());
      vo.setWarningDays(config.getWarningDays());
      vo.setMaxLoginAttempts(config.getMaxLoginAttempts());
      vo.setLockoutDuration(config.getLockoutDuration());
    }
    return vo;
  }

  public static LoginSecurityConfig toTwoFactorAuthConfig(LoginSecurityConfigUpdateDto dto,
      LoginSecurityConfig existingConfig) {
    LoginSecurityConfig config =
        existingConfig != null ? existingConfig : new LoginSecurityConfig();

    if (dto.getMaxLoginAttempts() != null) {
      config.setMaxLoginAttempts(dto.getMaxLoginAttempts());
    }
    if (dto.getAccountLockoutDurationMinutes() != null) {
      config.setAccountLockoutDurationMinutes(dto.getAccountLockoutDurationMinutes());
    }
    if (dto.getSessionTimeoutMinutes() != null) {
      config.setSessionTimeoutMinutes(dto.getSessionTimeoutMinutes());
    }
    if (dto.getTwoFactorAuthMethod() != null) {
      config.setTwoFactorAuthMethod(dto.getTwoFactorAuthMethod());
    }
    if (dto.getGraphicalCaptchaEnabled() != null) {
      config.setGraphicalCaptchaEnabled(dto.getGraphicalCaptchaEnabled());
    }
    if (dto.getSignInTypes() != null) {
      config.setSignInTypes(dto.getSignInTypes());
    }
    if (dto.getDefaultSignInType() != null) {
      config.setDefaultSignInType(dto.getDefaultSignInType());
    }
    if (dto.getCodeExpiration() != null) {
      config.setCodeExpiration(dto.getCodeExpiration());
    }
    if (dto.getTrustDeviceDays() != null) {
      config.setTrustDeviceDays(dto.getTrustDeviceDays());
    }
    if (dto.getTwoFactorEnabled() != null) {
      config.setTwoFactorEnabled(dto.getTwoFactorEnabled());
    }
    if (dto.getEnforceTwoFactorForAdmins() != null) {
      config.setEnforceTwoFactorForAdmins(dto.getEnforceTwoFactorForAdmins());
    }
    if (dto.getEnforceTwoFactorForAllUsers() != null) {
      config.setEnforceTwoFactorForAllUsers(dto.getEnforceTwoFactorForAllUsers());
    }
    if (dto.getAllowRegistrationEnabled() != null) {
      config.setAllowRegistrationEnabled(dto.getAllowRegistrationEnabled());
    }
    return config;
  }

  public static LoginSecurityConfigVo toLoginSecurityConfigVo(Security security) {
    LoginSecurityConfigVo vo = new LoginSecurityConfigVo();
    vo.setId(security.getId());
    vo.setStatus(nullSafe(security.getStatus(), EnabledStatus.ENABLED));

    if (security.getConfig() instanceof LoginSecurityConfig config) {
      vo.setMaxLoginAttempts(config.getMaxLoginAttempts());
      vo.setAccountLockoutDurationMinutes(config.getAccountLockoutDurationMinutes());
      vo.setSessionTimeoutMinutes(config.getSessionTimeoutMinutes());
      vo.setTwoFactorAuthMethod(config.getTwoFactorAuthMethod());
      vo.setTwoFactorEnabled(config.getTwoFactorEnabled());
      vo.setGraphicalCaptchaEnabled(config.getGraphicalCaptchaEnabled());
      vo.setSignInTypes(config.getSignInTypes());
      vo.setDefaultSignInType(config.getDefaultSignInType());
      vo.setCodeExpiration(config.getCodeExpiration());
      vo.setTrustDeviceDays(config.getTrustDeviceDays());
      vo.setEnforceTwoFactorForAdmins(config.getEnforceTwoFactorForAdmins());
      vo.setEnforceTwoFactorForAllUsers(config.getEnforceTwoFactorForAllUsers());
      vo.setAllowRegistrationEnabled(config.getAllowRegistrationEnabled());
    }
    return vo;
  }

  public static IpWhitelistConfig toIpWhitelistConfig(IpWhitelistCreateDto dto) {
    IpWhitelistConfig config = new IpWhitelistConfig();
    config.setIpAddress(dto.getIpAddress());
    config.setIpRange(dto.getIpRange());
    config.setDescription(dto.getDescription());
    config.setStatus(nullSafe(config.getStatus(), EnabledStatus.DISABLED));
    config.setLastUsed(null);
    config.setUsageCount(0L);
    return config;
  }

  public static IpWhitelistConfig toIpWhitelistConfig(IpWhitelistUpdateDto dto,
      IpWhitelistConfig existingConfig) {
    IpWhitelistConfig config = existingConfig != null ? existingConfig : new IpWhitelistConfig();

    if (dto.getIpAddress() != null) {
      config.setIpAddress(dto.getIpAddress());
    }
    if (dto.getIpRange() != null) {
      config.setIpRange(dto.getIpRange());
    }
    if (dto.getDescription() != null) {
      config.setDescription(dto.getDescription());
    }
    config.setStatus(nullSafe(config.getStatus(), EnabledStatus.DISABLED));
    return config;
  }

  public static IpWhitelistVo toIpWhitelistVo(Security security) {
    IpWhitelistVo vo = new IpWhitelistVo();
    vo.setId(security.getId());

    if (security.getConfig() instanceof IpWhitelistConfig config) {
      vo.setIpAddress(config.getIpAddress());
      vo.setIpRange(config.getIpRange());
      vo.setDescription(config.getDescription());
      vo.setStatus(nullSafe(config.getStatus(), EnabledStatus.DISABLED));
      vo.setLastUsed(config.getLastUsed());
      vo.setUsageCount(config.getUsageCount());
    }
    return vo;
  }

  public static List<IpWhitelistVo> toIpWhitelistVoList(List<Security> securities) {
    return securities.stream()
        .map(SecurityAssembler::toIpWhitelistVo)
        .collect(Collectors.toList());
  }

  public static SecurityNotificationConfig toNotificationConfig(
      SecurityNotificationConfigUpdateDto dto,
      SecurityNotificationConfig existingConfig) {
    SecurityNotificationConfig config =
        existingConfig != null ? existingConfig : new SecurityNotificationConfig();

    if (dto.getUserCriticalOperationNotify() != null) {
      config.setUserCriticalOperationNotify(dto.getUserCriticalOperationNotify());
    }
    if (dto.getSystemLoadHighNotify() != null) {
      config.setSystemLoadHighNotify(dto.getSystemLoadHighNotify());
    }
    if (dto.getServiceComponentAbnormalNotify() != null) {
      config.setServiceComponentAbnormalNotify(dto.getServiceComponentAbnormalNotify());
    }
    if (dto.getLoginFailureNotify() != null) {
      config.setLoginFailureNotify(dto.getLoginFailureNotify());
    }
    if (dto.getNewUserRegisterNotify() != null) {
      config.setNewUserRegisterNotify(dto.getNewUserRegisterNotify());
    }
    if (dto.getRecipientUsers() != null) {
      config.setRecipientUsers(dto.getRecipientUsers().stream()
          .map(u -> new RecipientUser(u.getId(), u.getName()))
          .collect(Collectors.toList()));
    }
    return config;
  }

  public static SecurityNotificationConfigVo toNotificationConfigVo(Security security) {
    SecurityNotificationConfigVo vo = new SecurityNotificationConfigVo();
    vo.setId(security.getId());
    vo.setStatus(nullSafe(security.getStatus(), EnabledStatus.ENABLED));

    if (security.getConfig() instanceof SecurityNotificationConfig config) {
      vo.setUserCriticalOperationNotify(config.getUserCriticalOperationNotify());
      vo.setSystemLoadHighNotify(config.getSystemLoadHighNotify());
      vo.setServiceComponentAbnormalNotify(config.getServiceComponentAbnormalNotify());
      vo.setLoginFailureNotify(config.getLoginFailureNotify());
      vo.setNewUserRegisterNotify(config.getNewUserRegisterNotify());
      vo.setRecipientUsers(config.getRecipientUsers() != null
          ? config.getRecipientUsers().stream()
          .map(u -> new RecipientUserDto(u.getId(), u.getName()))
          .collect(Collectors.toList())
          : null);
    }
    return vo;
  }
}
