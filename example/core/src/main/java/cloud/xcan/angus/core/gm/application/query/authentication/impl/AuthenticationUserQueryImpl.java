package cloud.xcan.angus.core.gm.application.query.authentication.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.TipMessage.LOGIN_PASSWORD_ERROR;
import static cloud.xcan.angus.remote.message.ProtocolException.M.ACCOUNT_PASSWORD_ERROR;
import static cloud.xcan.angus.remote.message.ProtocolException.M.ACCOUNT_PASSWORD_ERROR_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.USER_DISABLED_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.USER_DISABLED_T;
import static cloud.xcan.angus.remote.message.ProtocolException.M.USER_EXPIRED_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.USER_EXPIRED_T;
import static cloud.xcan.angus.remote.message.ProtocolException.M.USER_LOCKED_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.USER_LOCKED_T;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.oauthuser.AuthenticationUser;
import cloud.xcan.angus.api.commonlink.oauthuser.AuthenticationUserRepo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.enums.UserSource;
import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthenticationUserQuery;
import cloud.xcan.angus.core.gm.application.query.ldap.LdapQuery;
import cloud.xcan.angus.core.gm.application.query.security.SecurityQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserSecurityQuery;
import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.domain.security.Security;
import cloud.xcan.angus.core.gm.domain.security.model.PasswordPolicyConfig;
import cloud.xcan.angus.core.gm.domain.user.UserSecurity;
import cloud.xcan.angus.core.gm.infra.ldap.LdapClientService;
import cloud.xcan.angus.core.gm.infra.ldap.LdapClientServiceFactory;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationUserQueryImpl implements AuthenticationUserQuery {

  @Resource
  @Qualifier("authenticationUserRepo")
  private AuthenticationUserRepo authenticationUserRepo;

  @Resource
  private PasswordEncoder passwordEncoder;

  @Resource
  private UserQuery userQuery;

  @Resource
  private LdapQuery ldapQuery;

  @Resource
  private SecurityQuery securityQuery;

  @Resource
  private UserSecurityQuery userSecurityQuery;

  @Resource
  private LdapClientServiceFactory ldapClientServiceFactory;

  @Resource
  private AuthenticationUserQuery self = this;

  @Override
  public List<AuthenticationUser> findByAccountAndPassword(String account, String password) {
    return new BizTemplate<List<AuthenticationUser>>(false) {
      List<AuthenticationUser> usersDb = null;

      @Override
      protected void checkParams() {
        // 验证账号是否存在
        usersDb = authenticationUserRepo.findByAccount(account);
      }

      @Override
      protected List<AuthenticationUser> process() {
        if (isEmpty(usersDb)) {
          return null;
        }
        // 过滤匹配密码的用户
        usersDb = usersDb.stream().filter(user -> {
          try {
            // 获取对应的User实体
            User userEntity = userQuery.findAndCheck(Long.valueOf(user.getId()));
            // 如果是LDAP同步用户，使用LDAP服务验证
            if (userEntity.getSource() == UserSource.LDAP_SYNC) {
              return self.verifyLdapPassword(userEntity, password);
            } else {
              // 普通用户使用常规密码验证
              return isNotEmpty(user.getPassword())
                  && passwordEncoder.matches(password, user.getPassword());
            }
          } catch (Exception e) {
            // 如果获取用户失败，使用常规密码验证
            return isNotEmpty(user.getPassword())
                && passwordEncoder.matches(password, user.getPassword());
          }
        }).collect(Collectors.toList());
        return usersDb;
      }
    }.execute();
  }

  @Override
  public void checkPassword(Long id, String password) {
    new BizTemplate<Void>() {
      AuthenticationUser authUser = null;
      User user = null;

      @Override
      protected void checkParams() {
        authUser = findAndCheck(id);
        // 获取对应的User实体
        try {
          user = userQuery.findAndCheck(id);
        } catch (Exception e) {
          // 如果获取失败，userEntity为null，将使用常规密码验证
        }
      }

      @Override
      protected Void process() {
        boolean passwordMatches;
        // 如果是LDAP同步用户，使用LDAP服务验证
        if (user != null && user.getSource() == UserSource.LDAP_SYNC) {
          passwordMatches = self.verifyLdapPassword(user, password);
        } else {
          // 普通用户使用常规密码验证
          passwordMatches = passwordEncoder.matches(password, authUser.getPassword());
        }
        assertTrue(passwordMatches, LOGIN_PASSWORD_ERROR);
        return null;
      }
    }.execute();
  }

  @Override
  public AuthenticationUser checkAndFindByAccount(Long userId, SignInType signinType,
      String account, String password) {
    AuthenticationUser finalUser = null;
    if (nonNull(userId)) {
      // 当账号存在多个租户或短信登录时，通过userId控制用户名登录
      // 多账号登录应设置userId
      finalUser = authenticationUserRepo.findById(userId).orElse(null);
    } else if (SignInType.ACCOUNT_PASSWORD.equals(signinType)) {
      // 当未指定userId时，默认使用第一个匹配密码的用户名登录
      List<AuthenticationUser> users = authenticationUserRepo.findByAccount(account);
      if (isNotEmpty(users)) {
        if (users.size() == 1) {
          return users.get(0);
        }
        // 多个用户时根据密码匹配查找
        for (AuthenticationUser user : users) {
          try {
            // 获取对应的User实体
            User userEntity = userQuery.findAndCheck(Long.valueOf(user.getId()));
            boolean passwordMatches;
            // 如果是LDAP同步用户，使用LDAP服务验证
            if (userEntity.getSource() == UserSource.LDAP_SYNC) {
              passwordMatches = self.verifyLdapPassword(userEntity, password);
            } else {
              // 普通用户使用常规密码验证
              passwordMatches = isNotEmpty(user.getPassword())
                  && passwordEncoder.matches(password, user.getPassword());
            }
            if (passwordMatches) {
              finalUser = user;
              break;
            }
          } catch (Exception e) {
            // 如果获取用户失败，使用常规密码验证
            assert user != null;
            if (isNotEmpty(user.getPassword())
                && passwordEncoder.matches(password, user.getPassword())) {
              finalUser = user;
              break;
            }
          }
        }
      }
    }
    assertTrue(nonNull(finalUser), ACCOUNT_PASSWORD_ERROR, ACCOUNT_PASSWORD_ERROR_KEY);
    return finalUser;
  }

  @Override
  public void checkHistoryPasswordExists(Long userId, String newPassword) {
    UserSecurity userSecurity = userSecurityQuery.findByUserId(userId);
    if (userSecurity != null && userSecurity.getPasswordHistory() != null) {
      Security security = securityQuery.getPasswordPolicy();
      PasswordPolicyConfig config = security.getConfig() instanceof PasswordPolicyConfig
          ? (PasswordPolicyConfig) security.getConfig()
          : new PasswordPolicyConfig();
      Integer preventReuse = nullSafe(config.getPreventReuse(), 6);
      List<String> passwordHistory = userSecurity.getPasswordHistory();
      // 检查新密码是否与历史密码重复
      for (String oldPasswordHash : passwordHistory) {
        if (passwordEncoder.matches(newPassword, oldPasswordHash)) {
          throw ProtocolException.of("新密码不能与最近{0}次使用的密码相同",
              new Object[]{preventReuse});
        }
      }
    }
  }

  @Override
  public void checkUserValid(AuthenticationUser user) {
    assertTrue(user.isEnabled(), USER_DISABLED_T, USER_DISABLED_KEY,
        new Object[]{user.getFullName()});

    assertTrue(user.isAccountNonLocked(), USER_LOCKED_T, USER_LOCKED_KEY,
        new Object[]{user.getFullName()});

    assertTrue(user.isAccountNonExpired(), USER_EXPIRED_T, USER_EXPIRED_KEY,
        new Object[]{user.getFullName()});
  }

  @Override
  public AuthenticationUser findAndCheck(Long id) {
    return authenticationUserRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "AuthenticationUser"));
  }

  @Override
  public AuthenticationUser findByUsername(String username) {
    return authenticationUserRepo.findByUsername(username);
  }

  @Override
  public List<AuthenticationUser> findByEmail(String email) {
    return authenticationUserRepo.findByEmail(email);
  }

  @Override
  public List<AuthenticationUser> findByMobile(String mobile) {
    return authenticationUserRepo.findByPhone(mobile);
  }

  /**
   * 使用LDAP服务验证密码
   */
  @Override
  public boolean verifyLdapPassword(User user, String password) {
    try {
      // 获取LDAP配置
      if (user.getLdapId() == null) {
        return false;
      }
      Ldap ldapConfig = ldapQuery.findAndCheck(user.getLdapId());
      if (ldapConfig == null || !ldapConfig.getEnabled()) {
        return false;
      }

      // 创建LDAP客户端服务
      LdapClientService ldapClientService = ldapClientServiceFactory.create(ldapConfig);

      // 使用LDAP服务进行密码验证
      LdapClientService.LdapAuthResult authResult = ldapClientService.authenticate(
          user.getUsername(), password);

      return authResult != null && authResult.isAuthenticated();
    } catch (Exception e) {
      // LDAP验证失败
      return false;
    }
  }
}
