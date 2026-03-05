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
@Table(name = "maven_metadata")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class MavenMetadataEntity extends TenantEntity<MavenMetadataEntity, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "artifact_id_ref")
  private Long artifactIdRef;

  @Column(name = "group_id", nullable = false, length = 500)
  private String groupId;

  @Column(name = "artifact_id", nullable = false, length = 255)
  private String artifactId;

  @Column(name = "version", nullable = false, length = 100)
  private String version;

  @Column(name = "packaging", length = 50)
  private String packaging = "jar";

  @Column(name = "classifier", length = 100)
  private String classifier;

  @Column(name = "extension", length = 50)
  private String extension = "jar";

  @Column(name = "is_snapshot")
  private Boolean isSnapshot = false;

  @Column(name = "snapshot_timestamp", length = 50)
  private String snapshotTimestamp;

  @Column(name = "snapshot_build_number")
  private Integer snapshotBuildNumber;

  @Column(name = "pom_content", columnDefinition = "TEXT")
  private String pomContent;

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
