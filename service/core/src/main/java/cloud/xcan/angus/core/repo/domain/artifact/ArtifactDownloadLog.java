package cloud.xcan.angus.core.repo.domain.artifact;

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
@Table(name = "artifact_download_log")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class ArtifactDownloadLog extends TenantEntity<ArtifactDownloadLog, Long> {

  @Id
  private Long id;

  @Column(name = "artifact_id", nullable = false)
  private Long artifactId;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "ip_address")
  private String ipAddress;

  @Column(name = "user_agent")
  private String userAgent;

  @Column(name = "download_date", nullable = false, updatable = false)
  private LocalDateTime downloadDate;

  @PrePersist
  public void prePersist() {
    if (this.downloadDate == null) {
      this.downloadDate = LocalDateTime.now();
    }
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
