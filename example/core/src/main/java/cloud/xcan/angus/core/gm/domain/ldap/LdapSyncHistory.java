package cloud.xcan.angus.core.gm.domain.ldap;

import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapSyncStatus;
import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapSyncType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * LDAP同步历史实体
 */
@Getter
@Setter
@Entity
@Table(name = "gm_ldap_sync_history")
public class LdapSyncHistory extends TenantAuditingEntity<LdapSyncHistory, Long> {

  @Id
  private Long id;

  @Column(name = "ldap_id", nullable = false)
  private Long ldapId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20, nullable = false)
  private LdapSyncStatus status;

  @Column(name = "start_time", nullable = false)
  private LocalDateTime startTime;

  @Column(name = "end_time")
  private LocalDateTime endTime;

  @Column(name = "duration")
  private Integer duration;

  @Column(name = "total_users", nullable = false)
  private Integer totalUsers = 0;

  @Column(name = "new_users", nullable = false)
  private Integer newUsers = 0;

  @Column(name = "updated_users", nullable = false)
  private Integer updatedUsers = 0;

  @Column(name = "deleted_users", nullable = false)
  private Integer deletedUsers = 0;

  @Column(name = "failed_users", nullable = false)
  private Integer failedUsers = 0;

  @Enumerated(EnumType.STRING)
  @Column(name = "sync_type", length = 20, nullable = false)
  private LdapSyncType syncType;

  @Column(name = "error_message", length = 1000)
  private String errorMessage;

  @Override
  public Long identity() {
    return id;
  }
}
