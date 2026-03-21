package cloud.xcan.angus.core.gm.application.cmd.department;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.department.Department;

public interface DepartmentCmd {

  /**
   * 创建部门
   */
  Department create(Department department);

  /**
   * 更新部门
   */
  Department update(Department department);

  /**
   * 更新部门状态
   */
  Department updateStatus(Long id, EnabledStatus status);

  /**
   * 更新部门负责人
   */
  Department updateLeader(Long id, Long leaderId);

  /**
   * 删除部门
   */
  void delete(Long id);

  /**
   * 根据租户ID删除所有部门
   */
  void deleteByTenantId(Long tenantId);
}
