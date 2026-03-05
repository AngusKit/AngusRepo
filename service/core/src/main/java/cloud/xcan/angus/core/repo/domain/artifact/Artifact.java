package cloud.xcan.angus.core.repo.domain.artifact;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "artifact")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class Artifact extends TenantEntity<Artifact, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "path")
  private String path;

  @Column(name = "version")
  private String version;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "size_bytes")
  private Long sizeBytes = 0L;

  @Column(name = "checksum")
  private String checksum;

  @Column(name = "downloads")
  private Integer downloads = 0;

  @Column(name = "stars")
  private Integer stars = 0;

  @Column(name = "license")
  private String license;

  @Column(name = "is_latest")
  private Boolean isLatest = false;

  @Column(name = "tags", columnDefinition = "JSON")
  private String tags;

  @Column(name = "versions", columnDefinition = "JSON")
  private String versions;

  @Column(name = "vulnerability", columnDefinition = "JSON")
  private String vulnerability;

  @Column(name = "metadata", columnDefinition = "JSON")
  private String metadata;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @Column(name = "modified_by")
  private Long modifiedBy;

  @Column(name = "modified_date")
  private LocalDateTime modifiedDate;

  @Transient
  private String repositoryName;

  @Transient
  private ArtifactFormat format;

  @Transient
  private List<String> parsedTags;

  @Transient
  private ArtifactVulnerability parsedVulnerability;

  @Transient
  private ArtifactMetadata parsedMetadata;

  @Override
  public Long identity() {
    return this.id;
  }
}
