package cloud.xcan.angus.core.repo.domain.access;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "access_rule")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class AccessRule extends TenantEntity<AccessRule, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "principal_type", nullable = false, length = 50)
  private AccessPrincipalType principalType;

  @Column(name = "principal_id")
  private String principalId;

  @Column(name = "enabled")
  private Boolean enabled = true;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Column(name = "priority")
  private Integer priority = 0;

  @Column(name = "permissions", columnDefinition = "JSON")
  private String permissions;

  @Column(name = "paths", columnDefinition = "JSON")
  private String paths;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @Column(name = "modified_by")
  private Long modifiedBy;

  @Column(name = "modified_date")
  private LocalDateTime modifiedDate;

  @Transient
  private String principalName;

  @PrePersist
  public void prePersist() {
    if (createdDate == null) {
      createdDate = LocalDateTime.now();
    }
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
