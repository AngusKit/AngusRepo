package cloud.xcan.angus.core.repo.domain.repository;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "repository")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class RepoEntity extends TenantEntity<RepoEntity, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "format", nullable = false, length = 50)
  private RepositoryFormat format;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 50)
  private RepositoryType type;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "artifacts")
  private Integer artifacts = 0;

  @Column(name = "size_bytes")
  private Long sizeBytes = 0L;

  @Column(name = "url")
  private String url;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 50)
  private RepositoryStatus status = RepositoryStatus.ONLINE;

  @Column(name = "remote_url")
  private String remoteUrl;

  @Column(name = "blob_store")
  private String blobStore;

  @Column(name = "settings", columnDefinition = "JSON")
  private String settings;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @Column(name = "modified_by")
  private Long modifiedBy;

  @Column(name = "modified_date")
  private LocalDateTime modifiedDate;

  @Transient
  private String creatorName;

  @Override
  public Long identity() {
    return this.id;
  }
}
