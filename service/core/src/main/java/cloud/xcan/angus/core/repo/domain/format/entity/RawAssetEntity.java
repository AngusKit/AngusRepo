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
@Table(name = "raw_asset")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class RawAssetEntity extends TenantEntity<RawAssetEntity, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "artifact_id_ref")
  private Long artifactIdRef;

  @Column(name = "path", nullable = false, length = 2000)
  private String path;

  @Column(name = "file_name", nullable = false, length = 500)
  private String fileName;

  @Column(name = "content_type")
  private String contentType;

  @Column(name = "size")
  private Long size;

  @Column(name = "sha256")
  private String sha256;

  @Column(name = "md5")
  private String md5;

  @Column(name = "etag")
  private String etag;

  @Column(name = "is_directory")
  private Boolean isDirectory = false;

  @Column(name = "storage_path", length = 2000)
  private String storagePath;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @Column(name = "modified_by")
  private Long modifiedBy;

  @Column(name = "modified_date")
  private LocalDateTime modifiedDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
