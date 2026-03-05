package cloud.xcan.angus.core.repo.domain.access;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "access_token")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class AccessToken extends TenantEntity<AccessToken, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "name")
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "token_hash", nullable = false, unique = true)
  private String tokenHash;

  @Column(name = "enabled")
  private Boolean enabled = true;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Column(name = "last_used")
  private LocalDateTime lastUsed;

  @Column(name = "usage_count")
  private Long usageCount = 0L;

  @Column(name = "permissions", columnDefinition = "JSON")
  private String permissions;

  @Column(name = "ip_whitelist", columnDefinition = "JSON")
  private String ipWhitelist;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @PrePersist
  public void prePersist() {
    if (createdDate == null) {
      createdDate = LocalDateTime.now();
    }
  }

  public boolean isExpired() {
    return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
