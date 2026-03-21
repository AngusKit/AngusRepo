package cloud.xcan.angus.api.commonlink.department;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "gm_department")
public class Department extends TenantAuditingEntity<Department, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 50, unique = true)
  private String code;

  @Column(name = "description", length = 500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private EnabledStatus status;

  @Column(name = "parent_id")
  private Long parentId;

  @Column(name = "level")
  private Integer level;

  @Column(name = "sort_order")
  private Integer sortOrder;

  @Column(name = "leader_id")
  private Long leaderId;

  // Non-persistent fields - for temporary associated data
  @Transient
  private String leaderName;
  @Transient
  private String leaderAvatar;
  @Transient
  private Long userCount;
  @Transient
  private String parentName;
  @Transient
  private String path;

  public boolean hasParent() {
    return parentId != null && parentId > 0;
  }

  public DepartmentInfo toDepartmentInfo() {
    return new DepartmentInfo().setId(id).setCode(code).setName(name)
        .setDescription(description).setStatus(status).setParentId(parentId)
        .setLevel(level).setSortOrder(sortOrder).setLeaderId(leaderId);
  }

  @Override
  public Long identity() {
    return id;
  }

}
