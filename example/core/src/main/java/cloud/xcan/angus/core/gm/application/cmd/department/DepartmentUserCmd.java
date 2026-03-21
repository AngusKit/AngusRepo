package cloud.xcan.angus.core.gm.application.cmd.department;

import java.util.Collection;
import java.util.List;

/**
 * 部门用户命令服务接口
 */
public interface DepartmentUserCmd {

  /**
   * 批量添加部门用户
   */
  int addUsers(Long departmentId, List<Long> userIds);

  /**
   * 转移部门用户
   */
  int transferUsers(Long sourceDepartmentId, Long targetDepartmentId, List<Long> userIds);

  /**
   * 移除部门用户
   */
  void removeUser(Long departmentId, Long userId);

  /**
   * 批量移除部门用户
   */
  void removeUsers(Long departmentId, List<Long> userIds);

  /**
   * 设置主部门
   */
  void setPrimaryDepartment(Long userId, Long departmentId);

  /**
   * 根据部门ID删除所有用户关系
   */
  void deleteByDepartmentId(Long departmentId);

  /**
   * 根据用户ID集合删除用户关系
   */
  void deleteByUserIds(Collection<Long> ids);
}
