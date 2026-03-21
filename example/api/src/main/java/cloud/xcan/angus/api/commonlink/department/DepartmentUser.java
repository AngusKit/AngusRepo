package cloud.xcan.angus.api.commonlink.department;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 部门用户关系实体
 */
@Setter
@Getter
@Entity
@Table(name = "gm_department_user")
public class DepartmentUser extends TenantAuditingEntity<DepartmentUser, Long> {

  @Id
  private Long id;

  @Column(name = "department_id", nullable = false)
  private Long departmentId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "is_primary", nullable = false)
  private Boolean isPrimary = false; // 是否为主部门

  @Column(name = "is_manager", nullable = false)
  private Boolean isManager = false; // 是否为部门负责人

  // Non-persistent fields - for temporary associated data
  @Transient
  private String userName;
  @Transient
  private String userAvatar;
  @Transient
  private String departmentName;
  @Transient
  private String departmentCode;

  @Override
  public Long identity() {
    return id;
  }
}
