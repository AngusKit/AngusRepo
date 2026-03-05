package cloud.xcan.angus.core.repo.domain.upload;

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
@Table(name = "upload_task")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class UploadTask extends TenantEntity<UploadTask, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Column(name = "file_size", nullable = false)
  private Long fileSize;

  @Column(name = "checksum")
  private String checksum;

  @Column(name = "path")
  private String path;

  @Column(name = "version")
  private String version;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private UploadStatus status = UploadStatus.PENDING;

  @Column(name = "upload_token", nullable = false, unique = true)
  private String uploadToken;

  @Column(name = "expires")
  private LocalDateTime expires;

  @Column(name = "enable_chunked", nullable = false)
  private Boolean enableChunked = false;

  @Column(name = "total_chunks", nullable = false)
  private Integer totalChunks = 0;

  @Column(name = "uploaded_chunks", nullable = false)
  private Integer uploadedChunks = 0;

  @Column(name = "progress", nullable = false)
  private Integer progress = 0;

  @Column(name = "artifact_id")
  private Long artifactId;

  @Column(name = "error_message", length = 2000)
  private String errorMessage;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @Transient
  private String repositoryName;

  @Override
  public Long identity() {
    return this.id;
  }

  @PrePersist
  protected void onCreate() {
    this.createdDate = LocalDateTime.now();
  }

  public int getProgressPercent() {
    if (totalChunks == null || totalChunks == 0) {
      return progress != null ? progress : 0;
    }
    return (int) ((uploadedChunks != null ? uploadedChunks : 0) * 100L / totalChunks);
  }

  public boolean isExpired() {
    return expires != null && LocalDateTime.now().isAfter(expires);
  }

  public boolean isActive() {
    return status != null && status.isActive();
  }

  public boolean isTerminal() {
    return status != null && status.isTerminal();
  }
}
