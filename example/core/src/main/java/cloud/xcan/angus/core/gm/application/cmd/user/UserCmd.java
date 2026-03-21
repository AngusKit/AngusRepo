package cloud.xcan.angus.core.gm.application.cmd.user;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.user.User;
import java.util.List;
import java.util.Set;

public interface UserCmd {

  /**
   * 创建用户
   */
  User create(User user);

  /**
   * 创建用户
   */
  User create0(User user);

  /**
   * 更新用户
   */
  User update(User user);

  /**
   * 更新用户启用状态
   */
  User updateEnableStatus(Long id, EnabledStatus status);

  /**
   * 更新用户锁定状态
   */
  User updateLockStatus(Long id, Boolean isLocked);

  /**
   * 删除用户
   */
  void delete(Long id);

  /**
   * 批量删除用户
   */
  void batchDelete(Set<Long> ids);

  /**
   * 内部方法：更新用户信息
   */
  void update0(User user);

  /**
   * 更新用户主部门
   */
  void updateMainDepartment(Long sourceDepartmentId, Long targetDepartmentId, List<Long> userIds);

  /**
   * 更新用户离线状态
   *
   * @param principalName 用户名
   */
  void updateOfflineStatusByUsername(String principalName);

  /**
   * 清除用户主部门
   */
  void clearMainDepartment(List<Long> userIds, Long departmentId);

  /**
   * 清除用户主部门
   */
  void clearMainDepartment(Long departmentId);

  /**
   * 根据租户ID删除所有用户
   */
  void deleteByTenantId(Long tenantId);

}
