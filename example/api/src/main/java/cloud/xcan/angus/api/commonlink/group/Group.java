package cloud.xcan.angus.api.commonlink.group;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.group.enums.GroupType;
import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
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

@Setter
@Getter
@Entity
@Table(name = "gm_group")
public class Group extends TenantAuditingEntity<Group, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 50, unique = true)
  private String code;

  @Column(name = "description", length = 500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 20)
  private GroupType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private EnabledStatus status;

  @Column(name = "owner_id")
  private Long ownerId;

  // Non-persistent fields
  @Transient
  private Long userCount;
  @Transient
  private UserBase owner;
  @Transient
  private List<Long> userIds;

  public GroupInfo toGroupInfo() {
    return new GroupInfo().setId(id).setCode(code).setName(name)
        .setDescription(description).setType(type).setStatus(status)
        .setOwnerId(ownerId);
  }

  @Override
  public Long identity() {
    return id;
  }

}
