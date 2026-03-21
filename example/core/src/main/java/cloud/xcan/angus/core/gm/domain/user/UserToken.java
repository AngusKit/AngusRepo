package cloud.xcan.angus.core.gm.domain.user;

import cloud.xcan.angus.api.commonlink.user.enums.TokenStatus;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
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
 * 用户令牌实体
 */
@Setter
@Getter
@Entity
@Table(name = "gm_user_token")
public class UserToken extends TenantAuditingEntity<UserToken, Long> {

  @Id
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "description", length = 500)
  private String description;

  @Column(name = "token", nullable = false, unique = true, length = 100)
  private String token;

  @Column(name = "app_id", nullable = false, length = 50)
  private Long appId;

  @Column(name = "app_code", nullable = false, length = 100)
  private String appCode;

  @Type(JsonType.class)
  @Column(name = "scopes", columnDefinition = "json")
  private List<String> scopes;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private TokenStatus status;

  @Column(name = "last_used_at")
  private LocalDateTime lastUsedAt;

  @Column(name = "usage_count")
  private Integer usageCount;

  @Column(name = "revoked_at")
  private LocalDateTime revokedAt;

  @Transient
  private String plainToken;

  @Override
  public Long identity() {
    return this.id;
  }
}
