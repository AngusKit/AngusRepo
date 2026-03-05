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
@Table(name = "npm_version")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class NpmVersionEntity extends TenantEntity<NpmVersionEntity, Long> {

  @Id
  private Long id;

  @Column(name = "package_id", nullable = false)
  private Long packageId;

  @Column(name = "version", nullable = false, length = 100)
  private String version;

  @Column(name = "metadata", columnDefinition = "JSON")
  private String metadata;

  @Column(name = "tarball_path", length = 1000)
  private String tarballPath;

  @Column(name = "tarball_size")
  private Long tarballSize;

  @Column(name = "shasum")
  private String shasum;

  @Column(name = "integrity", length = 500)
  private String integrity;

  @Column(name = "unpacked_size")
  private Long unpackedSize;

  @Column(name = "file_count")
  private Integer fileCount;

  @Column(name = "deprecated", length = 500)
  private String deprecated;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
