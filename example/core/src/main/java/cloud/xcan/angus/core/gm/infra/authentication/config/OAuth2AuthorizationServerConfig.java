package cloud.xcan.angus.core.gm.infra.authentication.config;

import static cloud.xcan.angus.security.authentication.password.OAuth2PasswordAuthenticationProviderUtils.DEFAULT_ENCODING_ID;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.enums.UserSource;
import cloud.xcan.angus.core.gm.application.query.ldap.LdapQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.infra.authentication.checker.CustomUserPreAuthenticationChecks;
import cloud.xcan.angus.core.gm.infra.authentication.service.JdbcUserAuthoritiesLazyServiceImpl;
import cloud.xcan.angus.core.gm.infra.authentication.service.RedisLinkSecretService;
import cloud.xcan.angus.core.gm.infra.ldap.LdapClientService;
import cloud.xcan.angus.core.gm.infra.ldap.LdapClientServiceFactory;
import cloud.xcan.angus.core.spring.SpringContextHolder;
import cloud.xcan.angus.security.authentication.service.LinkSecretService;
import cloud.xcan.angus.security.repository.JdbcUserAuthoritiesLazyService;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

@Configuration
public class OAuth2AuthorizationServerConfig {

  /**
   * 支持邮箱验证码和短信验证码登录
   */
  @Bean
  public LinkSecretService linkSecretService() {
    return new RedisLinkSecretService();
  }

  /**
   * 支持授权策略、资源和操作角色权限的延迟初始化和加载
   */
  @Bean
  public JdbcUserAuthoritiesLazyService jdbcUserAuthoritiesLazyService() {
    return new JdbcUserAuthoritiesLazyServiceImpl();
  }

  /**
   * 支持允许客户自定义检查
   */
  @Bean
  public CustomUserPreAuthenticationChecks defaultPreAuthenticationChecks() {
    return new CustomUserPreAuthenticationChecks();
  }

  /**
   * 扩展LDAP登录支持
   */
  @Bean
  @Primary
  public PasswordEncoder passwordEncoder() {
    return createDelegatingPasswordEncoder();
  }

  /**
   * 创建带有默认映射的{@link DelegatingPasswordEncoder}。 可以添加其他映射，编码将更新以符合最佳实践。
   * 但是，由于{@link DelegatingPasswordEncoder}的特性，更新不应影响用户。 当前的映射包括：
   *
   * <ul>
   * <li>bcrypt - {@link BCryptPasswordEncoder}（也用于编码）</li>
   * <li>ldap -
   * {@link org.springframework.security.crypto.password.LdapShaPasswordEncoder}</li>
   * <li>MD4 -
   * {@link org.springframework.security.crypto.password.Md4PasswordEncoder}</li>
   * <li>MD5 - {@code new MessageDigestPasswordEncoder("MD5")}</li>
   * <li>noop -
   * {@link org.springframework.security.crypto.password.NoOpPasswordEncoder}</li>
   * <li>pbkdf2 - {@link Pbkdf2PasswordEncoder}</li>
   * <li>scrypt - {@link SCryptPasswordEncoder}</li>
   * <li>SHA-1 - {@code new MessageDigestPasswordEncoder("SHA-1")}</li>
   * <li>SHA-256 - {@code new MessageDigestPasswordEncoder("SHA-256")}</li>
   * <li>sha256 -
   * {@link org.springframework.security.crypto.password.StandardPasswordEncoder}</li>
   * <li>argon2 - {@link Argon2PasswordEncoder}</li>
   * </ul>
   *
   * @return 要使用的{@link PasswordEncoder}
   */
  @SuppressWarnings("deprecation")
  public static PasswordEncoder createDelegatingPasswordEncoder() {
    Map<String, PasswordEncoder> encoders = new HashMap<>();
    encoders.put(DEFAULT_ENCODING_ID, new BCryptPasswordEncoder());
    //encoders.put("ldap", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
    encoders.put("SHA", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
    encoders.put("MD4", new org.springframework.security.crypto.password.Md4PasswordEncoder());
    encoders.put("MD5",
        new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("MD5"));
    encoders.put("noop",
        org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
    encoders.put("pbkdf2", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_5());
    encoders.put("pbkdf2@SpringSecurity_v5_8",
        Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8());
    encoders.put("scrypt", SCryptPasswordEncoder.defaultsForSpringSecurity_v4_1());
    encoders.put("scrypt@SpringSecurity_v5_8",
        SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8());
    encoders.put("SHA-1",
        new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1"));
    encoders.put("SHA-256",
        new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256"));
    encoders
        .put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
    //encoders.put("argon2", new Argon2PasswordEncoder());
    encoders.put("LDAP-PROXY", LdapPasswordConnection.getInstance());
    return new DelegatingPasswordEncoder(DEFAULT_ENCODING_ID, encoders);
  }
}

@Slf4j
final class LdapPasswordConnection implements PasswordEncoder {

  private static final LdapPasswordConnection INSTANCE = new LdapPasswordConnection();

  private LdapPasswordConnection() {
  }

  @Override
  public String encode(CharSequence rawPassword) {
    return rawPassword.toString();
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    try {
      Long userId;
      String username;

      int colonIdx = encodedPassword.indexOf(':');
      if (colonIdx > 0) {
        userId = Long.valueOf(encodedPassword.substring(0, colonIdx));
        username = encodedPassword.substring(colonIdx + 1);
      } else {
        userId = Long.valueOf(encodedPassword);
        username = null;
      }

      if (username == null || username.isEmpty()) {
        log.warn("LDAP-PROXY: 用户名为空");
        return false;
      }

      return verifyLdapPassword(userId, username, rawPassword.toString());
    } catch (Exception e) {
      log.warn("LDAP密码验证失败: {}", e.getMessage());
      return false;
    }
  }

  public static LdapPasswordConnection getInstance() {
    return INSTANCE;
  }

  /**
   * 使用LDAP服务验证密码
   *
   * @param userId   用户ID
   * @param username 用户名
   * @param password 密码
   * @return 验证是否成功
   */
  private boolean verifyLdapPassword(Long userId, String username, String password) {
    try {
      // 通过Spring上下文获取服务
      UserQuery userQuery = SpringContextHolder.getBean(UserQuery.class);
      LdapQuery ldapQuery = SpringContextHolder.getBean(LdapQuery.class);
      LdapClientServiceFactory ldapClientServiceFactory =
          SpringContextHolder.getBean(LdapClientServiceFactory.class);

      // 获取用户信息
      User user = userQuery.findAndCheck(userId);
      if (user == null) {
        return false;
      }

      // 检查用户是否是LDAP同步用户
      if (user.getSource() != UserSource.LDAP_SYNC) {
        return false;
      }

      // 获取LDAP配置
      if (user.getLdapId() == null) {
        log.warn("用户未关联LDAP配置");
        return false;
      }
      Ldap ldapConfig = ldapQuery.findAndCheck(user.getLdapId());
      if (ldapConfig == null || !ldapConfig.getEnabled()) {
        log.warn("LDAP配置不存在或未启用");
        return false;
      }

      // 创建LDAP客户端服务
      LdapClientService ldapClientService = ldapClientServiceFactory.create(ldapConfig);

      // 使用LDAP服务进行密码验证
      LdapClientService.LdapAuthResult authResult =
          ldapClientService.authenticate(username, password);

      return authResult != null && authResult.isAuthenticated();
    } catch (Exception e) {
      log.warn("LDAP密码验证异常: {}", e.getMessage(), e);
      return false;
    }
  }

}
