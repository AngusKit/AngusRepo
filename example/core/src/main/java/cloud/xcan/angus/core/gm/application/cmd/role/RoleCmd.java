package cloud.xcan.angus.core.gm.application.cmd.role;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.role.PermissionInfo;
import cloud.xcan.angus.api.commonlink.role.Role;
import java.util.List;

public interface RoleCmd {

  /**
   * 创建角色
   */
  Role create(Role role);

  /**
   * 更新角色
   */
  Role update(Role role);

  /**
   * 更新角色状态
   */
  Role updateStatus(Long id, EnabledStatus status);

  /**
   * 删除角色
   */
  void delete(Long id);

  /**
   * 更新角色权限
   */
  Role updatePermissions(Long id, List<PermissionInfo> permissions);

  /**
   * 设置成默认角色
   */
  Role setDefault(Long id, Boolean isDefault);

  /**
   * 不带事务的更新角色信息
   */
  void update0(Role role);

  /**
   * 根据应用ID删除所有角色（所有应用租户自定义角色和授权也应该被删除）
   */
  void deleteByApplicationId(Long appId);
}
