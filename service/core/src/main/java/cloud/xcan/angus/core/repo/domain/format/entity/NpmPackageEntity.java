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
@Table(name = "npm_package")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class NpmPackageEntity extends TenantEntity<NpmPackageEntity, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "artifact_id_ref")
  private Long artifactIdRef;

  @Column(name = "name", nullable = false, length = 500)
  private String name;

  @Column(name = "scope")
  private String scope;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "readme", columnDefinition = "TEXT")
  private String readme;

  @Column(name = "license")
  private String license;

  @Column(name = "homepage", length = 500)
  private String homepage;

  @Column(name = "repository_url", length = 500)
  private String repositoryUrl;

  @Column(name = "keywords", columnDefinition = "JSON")
  private String keywords;

  @Column(name = "dist_tags", columnDefinition = "JSON")
  private String distTags;

  @Column(name = "maintainers", columnDefinition = "JSON")
  private String maintainers;

  @Column(name = "time_info", columnDefinition = "JSON")
  private String timeInfo;

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
