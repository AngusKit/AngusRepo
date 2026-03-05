package cloud.xcan.angus.core.repo.domain.upload;

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
@Table(name = "upload_chunk")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class UploadChunk extends TenantEntity<UploadChunk, Long> {

  @Id
  private Long id;

  @Column(name = "upload_task_id", nullable = false)
  private Long uploadTaskId;

  @Column(name = "chunk_index", nullable = false)
  private Integer chunkIndex;

  @Column(name = "chunk_size", nullable = false)
  private Long chunkSize;

  @Column(name = "checksum")
  private String checksum;

  @Column(name = "uploaded_date", nullable = false, updatable = false)
  private LocalDateTime uploadedDate;

  @Override
  public Long identity() {
    return this.id;
  }

  @PrePersist
  protected void onCreate() {
    this.uploadedDate = LocalDateTime.now();
  }
}
