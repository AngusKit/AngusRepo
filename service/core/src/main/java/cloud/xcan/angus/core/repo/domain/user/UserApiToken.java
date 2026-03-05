package cloud.xcan.angus.core.repo.domain.user;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "user_api_token")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class UserApiToken extends TenantEntity<UserApiToken, Long> {

  @Id
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "token_hash", nullable = false, unique = true)
  private String tokenHash;

  @Enumerated(EnumType.STRING)
  @Column(name = "permission", nullable = false, length = 50)
  private TokenPermission permission;

  @Column(name = "enabled")
  private Boolean enabled = true;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Column(name = "last_used")
  private LocalDateTime lastUsed;

  @Column(name = "usage_count")
  private Long usageCount = 0L;

  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
