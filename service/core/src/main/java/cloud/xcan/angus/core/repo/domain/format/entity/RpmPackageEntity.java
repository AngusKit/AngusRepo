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
@Table(name = "rpm_package")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class RpmPackageEntity extends TenantAuditingEntity<RpmPackageEntity, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "artifact_id_ref")
  private Long artifactIdRef;

  @Column(name = "name", nullable = false, length = 500)
  private String name;

  @Column(name = "epoch")
  private Integer epoch = 0;

  @Column(name = "version", nullable = false, length = 100)
  private String version;

  @Column(name = "release_ver", nullable = false, length = 100)
  private String releaseVer;

  @Column(name = "arch", nullable = false, length = 50)
  private String arch;

  @Column(name = "summary", columnDefinition = "TEXT")
  private String summary;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "url", length = 500)
  private String url;

  @Column(name = "license")
  private String license;

  @Column(name = "vendor")
  private String vendor;

  @Column(name = "rpm_group")
  private String rpmGroup;

  @Column(name = "source_rpm", length = 500)
  private String sourceRpm;

  @Column(name = "build_time")
  private Long buildTime;

  @Column(name = "installed_size")
  private Long installedSize;

  @Column(name = "archive_size")
  private Long archiveSize;

  @Column(name = "checksum")
  private String checksum;

  @Column(name = "checksum_type", length = 50)
  private String checksumType = "sha256";

  @Column(name = "header_range_start")
  private Long headerRangeStart;

  @Column(name = "header_range_end")
  private Long headerRangeEnd;

  @Column(name = "location_href", length = 1000)
  private String locationHref;

  @Column(name = "requires", columnDefinition = "JSON")
  private String requires;

  @Column(name = "provides", columnDefinition = "JSON")
  private String rpmProvides;

  @Column(name = "conflicts", columnDefinition = "JSON")
  private String rpmConflicts;

  @Column(name = "obsoletes", columnDefinition = "JSON")
  private String obsoletes;

  @Column(name = "files", columnDefinition = "JSON")
  private String files;

  @Column(name = "changelogs", columnDefinition = "JSON")
  private String changelogs;

  @Override
  public Long identity() {
    return this.id;
  }
}
