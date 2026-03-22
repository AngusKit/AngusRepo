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
@Table(name = "apt_package")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class AptPackageEntity extends TenantAuditingEntity<AptPackageEntity, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "artifact_id_ref")
  private Long artifactIdRef;

  @Column(name = "package_name", nullable = false, length = 500)
  private String packageName;

  @Column(name = "version", nullable = false)
  private String version;

  @Column(name = "architecture", nullable = false, length = 50)
  private String architecture;

  @Column(name = "distribution", nullable = false, length = 100)
  private String distribution;

  @Column(name = "component", nullable = false, length = 100)
  private String component = "main";

  @Column(name = "section", length = 100)
  private String section;

  @Column(name = "priority", length = 50)
  private String priority = "optional";

  @Column(name = "installed_size")
  private Long installedSize;

  @Column(name = "maintainer", length = 500)
  private String maintainer;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "depends", columnDefinition = "TEXT")
  private String depends;

  @Column(name = "pre_depends", columnDefinition = "TEXT")
  private String preDepends;

  @Column(name = "recommends", columnDefinition = "TEXT")
  private String recommends;

  @Column(name = "suggests", columnDefinition = "TEXT")
  private String suggests;

  @Column(name = "conflicts", columnDefinition = "TEXT")
  private String conflicts;

  @Column(name = "provides", columnDefinition = "TEXT")
  private String provides;

  @Column(name = "homepage", length = 500)
  private String homepage;

  @Column(name = "filename", nullable = false, length = 1000)
  private String filename;

  @Column(name = "size", nullable = false)
  private Long size;

  @Column(name = "md5sum")
  private String md5sum;

  @Column(name = "sha1")
  private String sha1;

  @Column(name = "sha256")
  private String sha256;

  @Override
  public Long identity() {
    return this.id;
  }
}
