package cloud.xcan.angus.core.gm.domain.authorization;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 授权角色关系实体。 表示授权与角色之间的多对多关系。
 */
@Setter
@Getter
@Entity
@Table(name = "gm_authorization_role")
public class AuthorizationRole extends TenantAuditingEntity<AuthorizationRole, Long> {

  @Id
  private Long id;

  @Column(name = "authorization_id", nullable = false)
  private Long authorizationId;

  @Column(name = "role_id", nullable = false)
  private Long roleId;

  @Override
  public Long identity() {
    return id;
  }
}
