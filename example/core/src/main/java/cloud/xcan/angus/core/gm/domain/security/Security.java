package cloud.xcan.angus.core.gm.domain.security;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.security.model.SecurityConfig;
import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Getter
@Setter
@Entity
@Table(name = "gm_security")
public class Security extends AuditingEntity<Security, Long> {

  @Id
  private Long id;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 50, nullable = false)
  private SecurityType type;

  @Column(name = "config", columnDefinition = "json")
  @Type(JsonType.class)
  private SecurityConfig config;

  @Enumerated(EnumType.STRING)
  @Column(name = "enabled", nullable = false)
  private EnabledStatus status;

  @Column(name = "description", length = 500)
  private String description;

  @Column(name = "version", nullable = false)
  private Integer version = 1;

  @Override
  public Long identity() {
    return id;
  }
}
