package cloud.xcan.angus.core.gm.domain.ldap;

import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapStatus;
import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Getter
@Setter
@Entity
@Table(name = "gm_ldap")
public class Ldap extends TenantAuditingEntity<Ldap, Long> {

  @Id
  private Long id;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 50, nullable = false)
  private LdapType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20, nullable = false)
  private LdapStatus status;

  @Column(name = "server_url", length = 500, nullable = false)
  private String serverUrl;

  @Column(name = "base_dn", length = 500, nullable = false)
  private String baseDn;

  @Column(name = "bind_dn", length = 500)
  private String bindDn;

  @Column(name = "bind_password", length = 500)
  private String bindPassword;

  @Column(name = "user_filter", length = 500)
  private String userFilter;

  @Column(name = "group_filter", length = 500)
  private String groupFilter;

  @Column(name = "sync_enabled", nullable = false)
  private Boolean syncEnabled = false;

  @Column(name = "enabled", nullable = false)
  private Boolean enabled = true;

  @Column(name = "description", length = 500)
  private String description;

  @Type(JsonType.class)
  @Column(name = "field_mapping", columnDefinition = "json")
  private Map<String, String> fieldMapping;

  @Override
  public Long identity() {
    return id;
  }
}
