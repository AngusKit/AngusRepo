package cloud.xcan.angus.api.commonlink.oauthuser;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.security.model.CustomOAuth2User;
import cloud.xcan.angus.spec.experimental.Resources;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

@Slf4j
@Entity
@Getter
@Setter
@Table(name = "oauth2_user")
public class AuthenticationUser extends CustomOAuth2User implements Resources<String> {
  // Build Fields -> Do in parent class.

  private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

  /**
   * OAuth2 User fields
   */
  protected String username;

  @Nullable // 需要用户设置密码
  protected String password;

  protected boolean enabled;

  @Column(name = "account_non_expired")
  protected boolean accountNonExpired;

  @Column(name = "account_non_locked")
  protected boolean accountNonLocked;

  /**
   * Is password none expired.
   */
  @Column(name = "credentials_non_expired")
  protected boolean credentialsNonExpired;

  @Transient
  protected Set<GrantedAuthority> authorities = new HashSet<>();

  /**
   * AngusGM User Info.
   */
  @Id
  protected String id;

  @Column(name = "first_name")
  protected String firstName;

  @Column(name = "last_name")
  protected String lastName;

  @Column(name = "full_name")
  protected String fullName;

  @Nullable
  protected String phone;

  @Nullable
  protected String email;

  @Column(name = "sys_admin")
  protected boolean sysAdmin;

  @Column(name = "password_expired_date")
  protected Instant passwordExpiredDate;

  @Column(name = "last_modified_password_date")
  protected Instant lastModifiedPasswordDate;

  @Column(name = "expired_date")
  protected Instant expiredDate;

  @Column(name = "tenant_id")
  protected String tenantId;

  @Column(name = "tenant_name")
  protected String tenantName;

  @Column(name = "default_language")
  protected String defaultLanguage;

  @Column(name = "default_time_zone")
  protected String defaultTimeZone;

  /**
   * Temp filed for signup.
   */
  @Transient
  protected String itc;
  @Transient
  protected String country;
  @JsonIgnore
  @Transient
  private String clientId;
  @JsonIgnore
  @Transient
  private String clientSource;

  /**
   * Temp filed for business.
   */
  @JsonIgnore
  @Transient
  protected String linkSecret; // 作为链接密钥
  @JsonIgnore
  @Transient
  protected boolean setPassword;

  public AuthenticationUser() {
  }

  public AuthenticationUser(String username, String password,
      Collection<? extends GrantedAuthority> authorities) {
    this(username, password, true, true, true, true, authorities);
  }

  public AuthenticationUser(String username, String password, boolean enabled,
      boolean accountNonExpired,
      boolean credentialsNonExpired, boolean accountNonLocked,
      Collection<? extends GrantedAuthority> authorities) {
    this(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
        authorities, "-1", null, null, null, false, null, null, null,
        null, null, "-1", null, null, null);
  }

  public AuthenticationUser(String username, String password, boolean enabled,
      boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
      Collection<? extends GrantedAuthority> authorities, String id, String firstName,
      String lastName, String fullName, boolean sysAdmin,
      String phone, String email, Instant passwordExpiredDate,
      Instant lastModifiedPasswordDate, Instant expiredDate, String tenantId,
      String tenantName, String defaultLanguage, String defaultTimeZone) {
    super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
        authorities, id, firstName, lastName, fullName, sysAdmin, phone,
        email, passwordExpiredDate, lastModifiedPasswordDate, expiredDate,
        tenantId, tenantName, defaultLanguage, defaultTimeZone);
    this.username = username;
    this.password = password;
    this.enabled = enabled;
    this.accountNonExpired = accountNonExpired;
    this.credentialsNonExpired = credentialsNonExpired;
    this.accountNonLocked = accountNonLocked;
    this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));

    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.fullName = fullName;
    this.sysAdmin = sysAdmin;
    this.phone = phone;
    this.email = email;
    this.passwordExpiredDate = passwordExpiredDate;
    this.lastModifiedPasswordDate = lastModifiedPasswordDate;
    this.expiredDate = expiredDate;
    this.tenantId = tenantId;
    this.tenantName = tenantName;
    this.defaultLanguage = defaultLanguage;
    this.defaultTimeZone = defaultTimeZone;
  }

  /**
   * Copy a User with AuthUserBuilder
   */
  public static CustomOAuth2User with(AuthenticationUser user) {
    return builder().username(user.username).password(user.password)
        .disabled(!user.enabled).accountExpired(!user.accountNonExpired)
        .accountLocked(!user.accountNonLocked).credentialsExpired(!user.credentialsNonExpired)
        .authorities(user.authorities).id(user.id).firstName(user.firstName).lastName(user.lastName)
        .fullName(user.fullName).sysAdmin(user.sysAdmin).phone(user.phone).email(user.email)
        .passwordExpiredDate(user.passwordExpiredDate)
        .lastModifiedPasswordDate(user.lastModifiedPasswordDate).expiredDate(user.expiredDate)
        .tenantId(user.tenantId).tenantName(user.tenantName)
        .build();
  }

  /**
   * Copy a User with AuthUserBuilder
   */
  public static AuthenticationUser with(String username, AuthenticationUser user,
      List<GrantedAuthority> authorities) {
    return newBuilder().username(username).password(user.password)
        .disabled(!user.enabled).accountExpired(!user.accountNonExpired)
        .accountLocked(!user.accountNonLocked).credentialsExpired(!user.credentialsNonExpired)
        .authorities(authorities).id(user.id).firstName(user.firstName).lastName(user.lastName)
        .fullName(user.fullName).sysAdmin(user.sysAdmin)
        .mobile(user.phone).email(user.email)
        .passwordExpiredDate(user.passwordExpiredDate)
        .lastModifiedPasswordDate(user.lastModifiedPasswordDate).expiredDate(user.expiredDate)
        .tenantId(user.tenantId).tenantName(user.tenantName)
        .build();
  }

  /**
   * Creates a AuthUserBuilder with a specified username
   *
   * @param username the username to use
   * @return the AuthUserBuilder
   */
  public static AuthUserBuilder username(String username) {
    return newBuilder().username(username);
  }

  /**
   * Creates a AuthUserBuilder
   *
   * @return the AuthUserBuilder
   */
  public static AuthUserBuilder newBuilder() {
    return new AuthUserBuilder();
  }

  @Deprecated
  public static AuthUserBuilder defaultPasswordEncoder() {
    log.warn("AuthenticationUser.withDefaultPasswordEncoder() is considered unsafe for production "
        + "and is only intended for sample applications.");
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    return newBuilder().passwordEncoder(encoder::encode);
  }

  public static AuthUserBuilder userDetails(UserDetails userDetails) {
    // @formatter:off
    return username(userDetails.getUsername())
        .password(userDetails.getPassword())
        .accountExpired(!userDetails.isAccountNonExpired())
        .accountLocked(!userDetails.isAccountNonLocked())
        .authorities(userDetails.getAuthorities())
        .credentialsExpired(!userDetails.isCredentialsNonExpired())
        .disabled(!userDetails.isEnabled());
    // @formatter:on
  }

  @Override
  public String getName() {
    return fullName;
  }

  private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    @Override
    public int compare(GrantedAuthority g1, GrantedAuthority g2) {
      if (g2.getAuthority() == null) {
        return -1;
      }
      if (g1.getAuthority() == null) {
        return 1;
      }
      return g1.getAuthority().compareTo(g2.getAuthority());
    }

  }

  public static final class AuthUserBuilder {

    /**
     * OAuth2 User fields
     */
    private String username;
    private String password;
    private boolean disabled = false;
    private boolean accountExpired = false;
    private boolean accountLocked = false;
    private boolean credentialsExpired = false;

    private List<GrantedAuthority> authorities = new ArrayList<>();
    private Function<String, String> passwordEncoder = (password) -> password;

    /**
     * AngusGM User Info.
     */
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private boolean sysAdmin = false;
    private String mobile;
    private String email;
    private Instant passwordExpiredDate;
    private Instant lastModifiedPasswordDate;
    private Instant expiredDate;
    private String tenantId;
    private String tenantName;

    private String defaultLanguage;
    private String defaultTimeZone;

    /**
     * Creates a new instance
     */
    private AuthUserBuilder() {
    }

    public AuthUserBuilder username(String username) {
      Assert.notNull(username, "username cannot be null");
      this.username = username;
      return this;
    }

    public AuthUserBuilder password(String password) {
      this.password = password;
      return this;
    }

    public AuthUserBuilder disabled(boolean disabled) {
      this.disabled = disabled;
      return this;
    }

    public AuthUserBuilder passwordEncoder(Function<String, String> encoder) {
      Assert.notNull(encoder, "encoder cannot be null");
      this.passwordEncoder = encoder;
      return this;
    }

    public AuthUserBuilder accountExpired(boolean accountExpired) {
      this.accountExpired = accountExpired;
      return this;
    }

    public AuthUserBuilder accountLocked(boolean accountLocked) {
      this.accountLocked = accountLocked;
      return this;
    }

    public AuthUserBuilder credentialsExpired(boolean credentialsExpired) {
      this.credentialsExpired = credentialsExpired;
      return this;
    }

    public AuthUserBuilder roles(String... roles) {
      List<GrantedAuthority> authorities = new ArrayList<>(roles.length);
      for (String role : roles) {
        Assert.isTrue(!role.startsWith("ROLE_"),
            () -> role + " cannot start with ROLE_ (it is automatically added)");
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
      }
      return authorities(authorities);
    }

    public AuthUserBuilder authorities(GrantedAuthority... authorities) {
      Assert.notNull(authorities, "authorities cannot be null");
      return authorities(Arrays.asList(authorities));
    }

    public AuthUserBuilder authorities(
        Collection<? extends GrantedAuthority> authorities) {
      Assert.notNull(authorities, "authorities cannot be null");
      this.authorities = new ArrayList<>(authorities);
      return this;
    }

    public AuthUserBuilder authorities(String... authorities) {
      Assert.notNull(authorities, "authorities cannot be null");
      return authorities(AuthorityUtils.createAuthorityList(authorities));
    }

    public AuthUserBuilder id(String id) {
      this.id = id;
      return this;
    }

    public AuthUserBuilder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public AuthUserBuilder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public AuthUserBuilder fullName(String fullName) {
      this.fullName = fullName;
      return this;
    }

    public AuthUserBuilder sysAdmin(boolean sysAdmin) {
      this.sysAdmin = sysAdmin;
      return this;
    }

    public AuthUserBuilder mobile(String mobile) {
      this.mobile = mobile;
      return this;
    }

    public AuthUserBuilder email(String email) {
      this.email = email;
      return this;
    }

    public AuthUserBuilder passwordExpiredDate(Instant passwordExpiredDate) {
      this.passwordExpiredDate = passwordExpiredDate;
      return this;
    }

    public AuthUserBuilder lastModifiedPasswordDate(Instant lastModifiedPasswordDate) {
      this.lastModifiedPasswordDate = lastModifiedPasswordDate;
      return this;
    }

    public AuthUserBuilder expiredDate(Instant expiredDate) {
      this.expiredDate = expiredDate;
      return this;
    }

    public AuthUserBuilder tenantId(String tenantId) {
      this.tenantId = tenantId;
      return this;
    }

    public AuthUserBuilder tenantName(String tenantName) {
      this.tenantName = tenantName;
      return this;
    }

    public AuthUserBuilder defaultLanguage(String defaultLanguage) {
      this.defaultLanguage = defaultLanguage;
      return this;
    }

    public AuthUserBuilder defaultTimeZone(String defaultTimeZone) {
      this.defaultTimeZone = defaultTimeZone;
      return this;
    }

    public AuthenticationUser build() {
      String encodedPassword = isNotEmpty(this.password)
          ? this.passwordEncoder.apply(this.password) : null;
      return new AuthenticationUser(this.username, encodedPassword, !this.disabled,
          !this.accountExpired, !this.credentialsExpired, !this.accountLocked, this.authorities,
          this.id, this.firstName, this.lastName, this.fullName,
          this.sysAdmin, this.mobile, this.email, this.passwordExpiredDate,
          this.lastModifiedPasswordDate, this.expiredDate, this.tenantId, this.tenantName,
          this.defaultLanguage, this.defaultTimeZone);
    }
  }
}
