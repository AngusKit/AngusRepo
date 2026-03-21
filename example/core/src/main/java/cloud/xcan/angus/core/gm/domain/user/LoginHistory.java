package cloud.xcan.angus.core.gm.domain.user;

import cloud.xcan.angus.api.commonlink.SuccessStatus;
import cloud.xcan.angus.api.enums.SignInType;
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
 * 用户登录历史记录实体
 */
@Setter
@Getter
@Entity
@Table(name = "gm_user_login_history")
public class LoginHistory extends EntitySupport<LoginHistory, Long> {

  @Id
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "username", length = 50)
  private String username;

  @Column(name = "login_time", nullable = false)
  private LocalDateTime loginTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "login_type", length = 20)
  private SignInType loginType;

  @Enumerated(EnumType.STRING)
  @Column(name = "login_status", nullable = false, length = 20)
  private SuccessStatus loginStatus;

  @Column(name = "ip_address", length = 50)
  private String ipAddress;

  @Column(name = "location", length = 100)
  private String location;

  @Column(name = "device", length = 80)
  private String device;

  @Column(name = "device_id", length = 100)
  private String deviceId;

  @Column(name = "user_agent", length = 400)
  private String userAgent;

  @Column(name = "failure_reason", length = 400)
  private String failureReason;

  @Override
  public Long identity() {
    return id;
  }
}
