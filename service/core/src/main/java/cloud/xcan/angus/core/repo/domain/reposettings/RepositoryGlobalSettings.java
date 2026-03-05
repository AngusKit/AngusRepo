package cloud.xcan.angus.core.repo.domain.reposettings;

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
@Table(name = "repository_global_settings")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class RepositoryGlobalSettings extends TenantEntity<RepositoryGlobalSettings, Long> {

  @Id
  private Long id;

  @Column(name = "default_repository")
  private String defaultRepository;

  @Column(name = "anonymous_access")
  private Boolean anonymousAccess = false;

  @Column(name = "indexing_enabled")
  private Boolean indexingEnabled = true;

  @Column(name = "compression_enabled")
  private Boolean compressionEnabled = true;

  @Column(name = "storage_quota_gb")
  private Long storageQuotaGb;

  @Column(name = "retention_days")
  private Integer retentionDays;

  @Column(name = "auto_cleanup")
  private Boolean autoCleanup = false;

  @Column(name = "deduplication_enabled")
  private Boolean deduplicationEnabled = false;

  @Column(name = "modified_by")
  private Long modifiedBy;

  @Column(name = "modified_date")
  private LocalDateTime modifiedDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
