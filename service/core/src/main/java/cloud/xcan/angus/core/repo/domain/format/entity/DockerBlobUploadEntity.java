package cloud.xcan.angus.core.repo.domain.format.entity;

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
@Table(name = "docker_blob_upload")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class DockerBlobUploadEntity extends TenantEntity<DockerBlobUploadEntity, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "uuid", nullable = false, unique = true, length = 255)
  private String uuid;

  @Column(name = "state", length = 50)
  private String state = "STARTED";

  @Column(name = "offset_bytes")
  private Long offsetBytes = 0L;

  @Column(name = "temp_path", length = 1000)
  private String tempPath;

  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @Column(name = "expires")
  private LocalDateTime expires;

  @Override
  public Long identity() {
    return this.id;
  }
}
