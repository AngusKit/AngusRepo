package cloud.xcan.angus.core.gm.domain.user;

import cloud.xcan.angus.core.gm.domain.user.enums.PasswordStrength;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

/**
 * 用户安全信息实体 注意：双因素认证相关字段（twoFactorEnabled, twoFactorSecret, backupCodes）已迁移到 UserSetting 中存储
 */
@Setter
@Getter
@Entity
@Table(name = "gm_user_security")
public class UserSecurity extends EntitySupport<UserSecurity, Long> {

  @Id
  private Long id;

  @Column(name = "user_id", nullable = false, unique = true)
  private Long userId;

  @Column(name = "password_last_changed")
  private LocalDateTime passwordLastChanged;

  @Enumerated(EnumType.STRING)
  @Column(name = "password_strength", length = 20)
  private PasswordStrength passwordStrength;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  @Column(name = "last_login_ip", length = 50)
  private String lastLoginIp;

  @Column(name = "last_login_location", length = 100)
  private String lastLoginLocation;

  @Column(name = "last_login_device", length = 200)
  private String lastLoginDevice;

  @Column(name = "last_login_device_id", length = 200)
  private String lastLoginDeviceId;

  @Type(JsonType.class)
  @Column(name = "password_history", columnDefinition = "json")
  private List<String> passwordHistory;

  @Transient
  private Boolean twoFactorEnabled;

  @Transient
  private String twoFactorSecret;

  @Transient
  private java.util.List<String> backupCodes;

  @Override
  public Long identity() {
    return id;
  }
}
