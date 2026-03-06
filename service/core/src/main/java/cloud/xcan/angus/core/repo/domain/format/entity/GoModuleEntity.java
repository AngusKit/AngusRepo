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
@Table(name = "go_module")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class GoModuleEntity extends TenantEntity<GoModuleEntity, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "artifact_id_ref")
  private Long artifactIdRef;

  @Column(name = "module_path", nullable = false, length = 1000)
  private String modulePath;

  @Column(name = "version", nullable = false, length = 100)
  private String version;

  @Column(name = "timestamp", nullable = false)
  private LocalDateTime timestamp;

  @Column(name = "go_mod_content", columnDefinition = "TEXT")
  private String goModContent;

  @Column(name = "go_version", length = 20)
  private String goVersion;

  @Column(name = "require_deps", columnDefinition = "JSON")
  private String requireDeps;

  @Column(name = "retract", columnDefinition = "JSON")
  private String retract;

  @Column(name = "replace_deps", columnDefinition = "JSON")
  private String replaceDeps;

  @Column(name = "exclude_deps", columnDefinition = "JSON")
  private String excludeDeps;

  @Column(name = "zip_path", length = 1000)
  private String zipPath;

  @Column(name = "zip_size")
  private Long zipSize;

  @Column(name = "zip_hash")
  private String zipHash;

  @Column(name = "mod_hash")
  private String modHash;

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
