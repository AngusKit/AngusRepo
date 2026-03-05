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
@Table(name = "docker_image")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class DockerImageEntity extends TenantEntity<DockerImageEntity, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "artifact_id_ref")
  private Long artifactIdRef;

  @Column(name = "image_name", nullable = false, length = 500)
  private String imageName;

  @Column(name = "tag", length = 255)
  private String tag;

  @Column(name = "digest", nullable = false, length = 255)
  private String digest;

  @Column(name = "manifest_media_type", length = 255)
  private String manifestMediaType;

  @Column(name = "manifest_content", columnDefinition = "MEDIUMTEXT")
  private String manifestContent;

  @Column(name = "config_digest", length = 255)
  private String configDigest;

  @Column(name = "total_size")
  private Long totalSize = 0L;

  @Column(name = "architecture", length = 50)
  private String architecture;

  @Column(name = "os", length = 50)
  private String os;

  @Column(name = "author", length = 255)
  private String author;

  @Column(name = "labels", columnDefinition = "JSON")
  private String labels;

  @Column(name = "created_by", nullable = false)
  private Long createdBy;

  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @Column(name = "modified_by", nullable = false)
  private Long modifiedBy;

  @Column(name = "modified_date")
  private LocalDateTime modifiedDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
