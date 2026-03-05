package cloud.xcan.angus.core.repo.domain.system;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "system_license")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class SystemLicense extends TenantEntity<SystemLicense, Long> {

  @Id
  private Long id;

  @Column(name = "license_type", nullable = false, length = 50)
  private String licenseType;

  @Column(name = "license_key", nullable = false, columnDefinition = "TEXT")
  private String licenseKey;

  @Column(name = "license_to")
  private String licenseTo;

  @Column(name = "issued_date")
  private LocalDateTime issuedDate;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Column(name = "max_users")
  private Integer maxUsers;

  @Column(name = "max_repositories")
  private Integer maxRepositories;

  @Column(name = "max_storage")
  private Long maxStorage;

  @Column(name = "features", columnDefinition = "JSON")
  private String features;

  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @Column(name = "modified_date")
  private LocalDateTime modifiedDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
