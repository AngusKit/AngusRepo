package cloud.xcan.angus.core.gm.domain.interfaces;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.interfaces.enums.InterfaceSyncAction;
import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import cloud.xcan.angus.spec.http.HttpMethod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "gm_interfaces")
public class Interface extends AuditingEntity<Interface, Long> {

  @Id
  private Long id;

  @Column(name = "service_name", nullable = false)
  private String serviceName;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  /**
   * 对应 OSA3 operationId
   **/
  @Column(name = "code", nullable = false, length = 100, unique = true)
  private String code;

  @Column(name = "path", nullable = false, length = 200)
  private String path;

  @Enumerated(EnumType.STRING)
  @Column(name = "method", length = 20)
  private HttpMethod method;

  @Column(name = "summary", length = 200)
  private String summary;

  @Column(name = "description", length = 400)
  private String description;

  /**
   * 注意：Angus应用只允许配置一个Swagger Tag值，用作授权资源编码(resource)
   */
  @Column(name = "tag")
  private String tag;

  @Column(name = "tag_description")
  private String tagDescription;

  @Column(name = "version", length = 20)
  private String version;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private EnabledStatus status;

  @Column(name = "deprecated")
  private Boolean deprecated;

  @Column(name = "deprecation_note")
  private String deprecationNote;

  @Column(name = "last_sync_time")
  private LocalDateTime lastSyncTime;

  /**
   * 最后一次同步操作类型（新增或更新）
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "last_sync_action", length = 20)
  private InterfaceSyncAction lastSyncAction;

  @Override
  public Long identity() {
    return id;
  }
}
