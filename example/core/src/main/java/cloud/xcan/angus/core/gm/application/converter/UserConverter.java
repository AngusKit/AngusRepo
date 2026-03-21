package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.core.gm.infra.utils.RegistrationInfoGenerator.generateTenantCode;
import static cloud.xcan.angus.core.gm.infra.utils.RegistrationInfoGenerator.generateUserName;
import static cloud.xcan.angus.core.gm.infra.utils.RegistrationInfoGenerator.generateUsername;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.enums.AccountType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.enums.UserSource;
import cloud.xcan.angus.api.commonlink.user.enums.UserStatus;
import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.core.gm.domain.user.UserInvite;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.UserSignupDto;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import java.util.List;

public class UserConverter {

  public static User toSignUpUser(UserSignupDto dto, UserInvite invite, Long tenantId) {
    // 创建用户实体
    User user = new User();
    user.setName(nullSafe(dto.getName(), generateUserName(dto.getEmail(), dto.getPhone())));
    user.setUsername(generateUsername(dto.getEmail(), dto.getPhone()));
    user.setEmail(dto.getEmail());
    if (SignInType.EMAIL_CODE.equals(dto.getRegisterType())){
      user.setEmailVerified(true);
    }
    user.setPhone(dto.getPhone());
    if (SignInType.SMS_CODE.equals(dto.getRegisterType())){
      user.setPhoneVerified(true);
    }
    user.setPassword(dto.getPassword()); // UserCmd.create 会加密密码

    user.setStatus(UserStatus.ACTIVE);
    user.setLocked(false);
    user.setSource(UserSource.PLATFORM_REGISTER);

    user.setTenantId(tenantId);

    // 如果有邀请码，设置租户、部门和角色
    if (invite != null) {
      user.setSysAdmin(false);
      user.setDepartmentId(invite.getDepartmentId());

      if (invite.getRoleId() != null) {
        user.setRoleIds(List.of(invite.getRoleId()));
      }
    } else {
      user.setSysAdmin(true);
    }
    return user;
  }

  public static Tenant toSignUpTenant(UserSignupDto dto) {
    String code = generateTenantCode(null, dto.getEmail(), null);
    Tenant tenant = new Tenant();
    tenant.setName(code);
    tenant.setCode(code);
    tenant.setType(null);
    tenant.setAccountType(AccountType.MAIN);
    tenant.setAdminName("Admin");
    tenant.setAdminEmail(dto.getEmail());
    tenant.setAdminPhone(dto.getPhone());
    tenant.setAddress(null);
    tenant.setStatus(EnabledStatus.ENABLED);
    tenant.setLogo(null);
    tenant.setExpireDate(null);
    tenant.setDefaultLanguage(Language.fromDefaultValue(PrincipalContext.getDefaultLanguage()));
    return tenant;
  }

  public static User toTenantAdminUser(Long tenantId, String name, String email, String phone) {
    User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setEmailVerified(false);
    user.setPhone(phone);
    user.setPhoneVerified(false);
    //user.setPassword(dto.getPassword());
    user.setStatus(UserStatus.ACTIVE);
    user.setSysAdmin(true);
    user.setLocked(false);
    user.setSource(UserSource.ADMIN_ADDED);
    user.setTenantId(tenantId);
    return user;
  }

}
