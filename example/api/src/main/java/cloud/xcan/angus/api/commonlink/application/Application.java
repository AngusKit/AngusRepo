package cloud.xcan.angus.api.commonlink.application;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationSource;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationType;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Setter
@Getter
@Entity
@Table(name = "gm_application")
public class Application extends AuditingEntity<Application, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 80, unique = true)
  private String code;

  @Column(name = "display_name", length = 40)
  private String displayName;

  @Column(name = "description", length = 400)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 20)
  private ApplicationType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "source", length = 20)
  private ApplicationSource source;

  /**
   * 应用安装路径，安装时初始化，用于备份与恢复
   */
  @Column(name = "installed_path", length = 400)
  private String installedPath;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private EnabledStatus status;

  @Column(name = "version", length = 20)
  private String version;

  @Enumerated(EnumType.STRING)
  @Column(name = "edition_type", length = 20)
  private EditionType editionType;

  @Column(name = "client_id", length = 100)
  private String clientId;

  @Column(name = "url", length = 200)
  private String url;

  @Column(name = "sort_order")
  private Integer sortOrder;

  @Type(JsonType.class)
  @Column(name = "tags", columnDefinition = "json")
  private List<String> tags;

  // Non-persistent fields
  @Transient
  private int menuCount;
  @Transient
  private int roleCount;
  @Transient
  private int userCount;
  @Transient
  private List<ApplicationMenu> menus;

  @Override
  public Long identity() {
    return id;
  }
}
