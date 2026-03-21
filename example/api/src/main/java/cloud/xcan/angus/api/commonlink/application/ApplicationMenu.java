package cloud.xcan.angus.api.commonlink.application;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationMenuType;
import cloud.xcan.angus.api.commonlink.role.PermissionInfo;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Setter
@Getter
@Entity
@Table(name = "gm_application_menu")
@EntityListeners({TenantListener.class})
public class ApplicationMenu extends TenantAuditingEntity<ApplicationMenu, Long> {

  @Id
  private Long id;

  @Column(name = "application_id", nullable = false)
  private Long applicationId;

  @Column(name = "code", nullable = false, length = 80)
  private String code;

  /**
   * 菜单完整名称
   */
  @Column(name = "name", nullable = false, length = 100)
  private String name;

  /**
   * 页面实际展示的简化名称
   */
  @Column(name = "show_name", nullable = false, length = 40)
  private String showName;

  @Column(name = "pid")
  private Long parentId;

  @Column(name = "icon", length = 200)
  private String icon;

  @Column(name = "path", length = 200)
  private String path;

  @Column(name = "sort_order")
  private Integer sortOrder;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private EnabledStatus status;

  @Column(name = "description", length = 400)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 10)
  private ApplicationMenuType type;

  /**
   * <p>
   * 是否启用权限控制。
   * </p>
   * <p>
   * 使用说明：
   * <ul>
   *   <li>true: 启用权限控制，需要检查 permission 字段对应的权限</li>
   *   <li>false: 不启用权限控制，所有用户都可以访问该菜单</li>
   *   <li>null: 默认为 false，不启用权限控制</li>
   * </ul>
   * </p>
   */
  @Column(name = "requires_auth", nullable = false)
  private Boolean requiresAuth;

  /**
   * <p>
   * 权限信息，用于权限验证。
   * </p>
   * <p>
   * 权限结构说明：
   * <ul>
   *   <li>resource: 资源标识，如 "users"、"tenants"、"departments" 等</li>
   *   <li>resourceName: 资源名称，用于显示</li>
   *   <li>actions: 操作列表，常用操作包括 "create"、"read"、"update"、"delete"</li>
   *   <li>description: 权限描述</li>
   * </ul>
   * </p>
   * <p>
   * 使用场景：
   * <ul>
   *   <li>当 authCtrl = true 时，此字段必须设置</li>
   *   <li>系统会检查用户所属角色的 permissions 字段，判断是否包含对应的 resource 和 action</li>
   *   <li>如果用户没有对应权限，该菜单项将不会显示或无法访问</li>
   * </ul>
   * </p>
   * <p>
   * 示例：
   * <pre>
   * {
   *   "resource": "users",
   *   "resourceName": "用户管理",
   *   "actions": ["read"],
   *   "description": "用户查看权限"
   * }
   * </pre>
   * </p>
   * <p>
   * 权限匹配规则：
   * <ul>
   *   <li>菜单 permission.resource="users" 且 permission.actions 包含 "read" 时，匹配角色 permissions 中 resource="users" 且 actions 包含 "read"</li>
   *   <li>如果 permission.actions 为空或 null，则匹配角色 permissions 中 resource 相同且 actions 不为空</li>
   *   <li>如果 authCtrl = false，则忽略此字段，所有用户都可以访问</li>
   * </ul>
   * </p>
   */
  @Type(JsonType.class)
  @Column(name = "permission", columnDefinition = "json")
  private PermissionInfo permission;

  @Override
  public Long identity() {
    return id;
  }
}

