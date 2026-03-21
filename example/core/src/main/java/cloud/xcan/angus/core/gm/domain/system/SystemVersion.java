package cloud.xcan.angus.core.gm.domain.system;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.domain.system.enums.VersionStatus;
import cloud.xcan.angus.core.gm.domain.system.enums.VersionType;
import cloud.xcan.angus.core.gm.domain.system.model.VersionBreakingChange;
import cloud.xcan.angus.core.gm.domain.system.model.VersionBugFix;
import cloud.xcan.angus.core.gm.domain.system.model.VersionFeature;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Getter
@Setter
@Entity
@Table(name = "gm_system_version", uniqueConstraints = {
    @UniqueConstraint(name = "uk_app_code_version", columnNames = {"app_code", "version"})
})
public class SystemVersion extends EntitySupport<SystemVersion, Long> {

  @Id
  private Long id;

  /**
   * 版本号（合并了原来的 versionNumber 和 versionName）
   */
  @Column(name = "version", length = 100, nullable = false)
  private String version;

  /**
   * 应用编码
   */
  @Column(name = "app_code", length = 80, nullable = false)
  private String appCode;

  /**
   * 版本类型
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 30, nullable = false)
  private VersionType type;

  /**
   * 版本类型
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "edition_type", length = 30, nullable = false)
  private EditionType editionType;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20, nullable = false)
  private VersionStatus status;

  @Column(name = "release_date")
  private LocalDateTime releaseDate;

  @Column(name = "changelog", columnDefinition = "text")
  private String changelog;

  /**
   * 特性列表
   */
  @Column(name = "features", columnDefinition = "json")
  @Type(JsonType.class)
  private List<VersionFeature> features;

  /**
   * Bug修复列表
   */
  @Column(name = "bug_fixes", columnDefinition = "json")
  @Type(JsonType.class)
  private List<VersionBugFix> bugFixes;

  /**
   * 破坏性变更列表
   */
  @Column(name = "breaking_changes", columnDefinition = "json")
  @Type(JsonType.class)
  private List<VersionBreakingChange> breakingChanges;

  @Column(name = "rollback_version", length = 50)
  private String rollbackVersion;

  @Column(name = "description", length = 1000)
  private String description;

  @Override
  public Long identity() {
    return id;
  }
}
