package cloud.xcan.angus.api.commonlink.role;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.role.enums.RoleEffect;
import cloud.xcan.angus.api.commonlink.user.UserInfo;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
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
@Table(name = "gm_role")
public class Role extends TenantAuditingEntity<Role, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 80, unique = true)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(name = "effect", length = 20)
  private RoleEffect effect;

  @Column(name = "description", length = 500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private EnabledStatus status;

  @Column(name = "is_system", nullable = false)
  private Boolean isSystem = false;

  /**
   * 应用默认角色针对所有用户自动生效
   */
  @Column(name = "is_default", nullable = false)
  private Boolean isDefault = false;

  @Column(name = "app_id", nullable = false)
  private Long appId;

  /**
   * <p>
   * 角色权限列表，以JSON格式存储。
   * </p>
   * <p>
   * 权限格式说明：
   * <ul>
   *   <li>menuId: 菜单ID，授权应用功能时必须</li>
   *   <li>menuName: 菜单名称（只读）</li>
   *   <li>resource: 资源标识，如 "users"、"tenants"、"departments" 等</li>
   *   <li>resourceName: 资源名称（只读）</li>
   *   <li>actions: 操作列表，常用操作包括 "create"、"read"、"update"、"delete"</li>
   *   <li>description: 权限描述（只读）</li>
   * </ul>
   * </p>
   * <p>
   * 示例：
   * <pre>
   * [
   *   {
   *     "menuId": 1001,
   *     "menuName": "用户管理",
   *     "resource": "users",
   *     "resourceName": "用户管理",
   *     "actions": ["create", "read", "update", "delete"],
   *     "description": "用户的增删改查"
   *   },
   *   {
   *     "menuId": 1002,
   *     "menuName": "租户管理",
   *     "resource": "tenants",
   *     "resourceName": "租户管理",
   *     "actions": ["read"],
   *     "description": "租户的查看"
   *   }
   * ]
   * </pre>
   * </p>
   * <p>
   * 权限验证规则：
   * <ul>
   *   <li>当用户访问需要权限的功能时，系统会检查用户所属角色是否包含对应的 resource 和 action</li>
   *   <li>ApplicationMenu 的 permission 字段（类型：PermissionInfo）会与此字段进行匹配</li>
   *   <li>例如：菜单 permission.resource="users" 且 permission.actions 包含 "read" 时，需要角色 permissions 中包含 resource="users" 且 actions 包含 "read"</li>
   *   <li>menuId 用于关联具体的应用菜单，授权应用功能时必须提供</li>
   * </ul>
   * </p>
   */
  @Type(JsonType.class)
  @Column(name = "permissions", columnDefinition = "json")
  private List<PermissionInfo> permissions;

  // Non-persistent fields
  @Transient
  private List<Long> resourceIdList;
  @Transient
  private Long authorizationCount;
  @Transient
  private Long userCount;
  @Transient
  private String appName;
  @Transient
  private List<UserInfo> users;

  public RoleInfo toRoleInfo() {
    return new RoleInfo().setId(id).setCode(code).setName(name)
        .setAppId(appId).setAppName(appName);
  }

  @Override
  public Long identity() {
    return id;
  }
}
