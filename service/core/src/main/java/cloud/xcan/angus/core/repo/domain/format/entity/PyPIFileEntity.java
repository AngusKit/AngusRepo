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
@Table(name = "pypi_file")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class PyPIFileEntity extends TenantEntity<PyPIFileEntity, Long> {

  @Id
  private Long id;

  @Column(name = "package_id", nullable = false)
  private Long packageId;

  @Column(name = "filename", nullable = false, length = 500)
  private String filename;

  @Column(name = "packagetype", length = 50)
  private String packagetype;

  @Column(name = "python_version", length = 50)
  private String pythonVersion;

  @Column(name = "requires_python", length = 100)
  private String requiresPython;

  @Column(name = "size")
  private Long size;

  @Column(name = "sha256_digest")
  private String sha256Digest;

  @Column(name = "md5_digest")
  private String md5Digest;

  @Column(name = "storage_path", length = 1000)
  private String storagePath;

  @Column(name = "yanked")
  private Boolean yanked = false;

  @Column(name = "yanked_reason", length = 500)
  private String yankedReason;

  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
