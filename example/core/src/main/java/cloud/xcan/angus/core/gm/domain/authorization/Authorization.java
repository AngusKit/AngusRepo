package cloud.xcan.angus.core.gm.domain.authorization;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.role.RoleInfo;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "gm_authorization")
public class Authorization extends TenantAuditingEntity<Authorization, Long> {

  @Id
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "subject_type", length = 20)
  private AuthorizationSubjectType subjectType;

  @Column(name = "subject_id", nullable = false)
  private Long subjectId;

  @Column(name = "subject_name", nullable = false)
  private String subjectName;

  @Column(name = "description", length = 400)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private EnabledStatus status;

  /**
   * <p>
   * 授权来源标志。
   * </p>
   * <p>
   * 使用说明：
   * <ul>
   *   <li>true: 通过开通或者私有化安装自动授权，不允许手动删除</li>
   *   <li>false: 手动授权，允许删除</li>
   *   <li>null: 默认为 false，手动授权</li>
   * </ul>
   * </p>
   * <p>
   * 与 status 字段的区别：
   * <ul>
   *   <li>opened: 表示授权的来源（自动授权 vs 手动授权），用于控制授权是否可删除</li>
   *   <li>status: 表示授权的启用/禁用状态，用于控制授权是否可用</li>
   * </ul>
   * </p>
   */
  @Column(name = "opened", nullable = false)
  private Boolean opened = false;

  @Column(name = "valid_from")
  private LocalDateTime validFrom;

  @Column(name = "valid_to")
  private LocalDateTime validTo;

  @Transient
  private List<RoleInfo> roleInfos;

  /**
   * <p>
   * 临时字段，用于在创建/更新操作时临时存储角色ID列表。此字段不会被持久化，用于将角色ID传递给命令层。
   * </p>
   */
  @Transient
  private List<Long> roleIds;

  /**
   * <p>
   * 临时字段，用于存储授权主体的用户数量。 此字段不会被持久化，用于将用户数量传递给VO层。
   * </p>
   * <p>
   * 用户数量计算规则：
   * <ul>
   *   <li>USER类型：1</li>
   *   <li>DEPARTMENT类型：部门下的用户数量</li>
   *   <li>GROUP类型：组下的用户数量</li>
   * </ul>
   * </p>
   */
  @Transient
  private Integer subjectUserCount;

  @Override
  public Long identity() {
    return id;
  }
}
