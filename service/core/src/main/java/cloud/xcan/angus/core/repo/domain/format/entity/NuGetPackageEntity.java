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
@Table(name = "nuget_package")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class NuGetPackageEntity extends TenantEntity<NuGetPackageEntity, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "artifact_id_ref")
  private Long artifactIdRef;

  @Column(name = "package_id", nullable = false, length = 500)
  private String packageId;

  @Column(name = "version", nullable = false, length = 100)
  private String version;

  @Column(name = "title", length = 500)
  private String title;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "authors", length = 1000)
  private String authors;

  @Column(name = "project_url", length = 500)
  private String projectUrl;

  @Column(name = "license", length = 500)
  private String license;

  @Column(name = "tags", length = 1000)
  private String tags;

  @Column(name = "is_prerelease")
  private Boolean isPrerelease = false;

  @Column(name = "is_listed")
  private Boolean isListed = true;

  @Column(name = "nuspec_content", columnDefinition = "TEXT")
  private String nuspecContent;

  @Column(name = "dependency_groups", columnDefinition = "JSON")
  private String dependencyGroups;

  @Column(name = "nupkg_path", length = 1000)
  private String nupkgPath;

  @Column(name = "nupkg_size")
  private Long nupkgSize;

  @Column(name = "hash")
  private String hash;

  @Column(name = "hash_algorithm", length = 50)
  private String hashAlgorithm = "SHA512";

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
