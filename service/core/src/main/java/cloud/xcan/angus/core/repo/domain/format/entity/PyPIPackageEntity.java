package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "pypi_package")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class PyPIPackageEntity extends TenantAuditingEntity<PyPIPackageEntity, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "artifact_id_ref")
  private Long artifactIdRef;

  @Column(name = "name", nullable = false, length = 500)
  private String name;

  @Column(name = "normalized_name", nullable = false, length = 500)
  private String normalizedName;

  @Column(name = "version", nullable = false, length = 100)
  private String version;

  @Column(name = "summary", columnDefinition = "TEXT")
  private String summary;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "description_content_type", length = 100)
  private String descriptionContentType;

  @Column(name = "author", length = 500)
  private String author;

  @Column(name = "author_email", length = 500)
  private String authorEmail;

  @Column(name = "license")
  private String license;

  @Column(name = "requires_python", length = 100)
  private String requiresPython;

  @Column(name = "homepage", length = 500)
  private String homepage;

  @Column(name = "classifiers", columnDefinition = "JSON")
  private String classifiers;

  @Column(name = "keywords", columnDefinition = "JSON")
  private String keywords;

  @Column(name = "requires_dist", columnDefinition = "JSON")
  private String requiresDist;

  @Override
  public Long identity() {
    return this.id;
  }
}
