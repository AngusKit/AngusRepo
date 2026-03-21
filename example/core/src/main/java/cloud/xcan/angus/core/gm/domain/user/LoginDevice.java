package cloud.xcan.angus.core.gm.domain.user;

import cloud.xcan.angus.api.enums.DeviceType;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 登录设备实体
 */
@Setter
@Getter
@Entity
@Table(name = "gm_user_login_device")
public class LoginDevice extends EntitySupport<LoginDevice, Long> {

  @Id
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "device_id", length = 100)
  private String deviceId;

  @Column(name = "device_name", length = 200)
  private String deviceName;

  @Enumerated(EnumType.STRING)
  @Column(name = "device_type", length = 20)
  private DeviceType deviceType;

  @Column(name = "browser", length = 50)
  private String browser;

  @Column(name = "browser_version", length = 20)
  private String browserVersion;

  @Column(name = "os", length = 50)
  private String os;

  @Column(name = "os_version", length = 20)
  private String osVersion;

  @Column(name = "ip_address", length = 50)
  private String ipAddress;

  @Column(name = "location", length = 100)
  private String location;

  @Column(name = "is_current")
  private Boolean isCurrent;

  @Column(name = "last_active_at")
  private LocalDateTime lastActiveAt;

  @Column(name = "user_agent", length = 500)
  private String userAgent;

  @Override
  public Long identity() {
    return id;
  }
}
