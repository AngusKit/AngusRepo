package cloud.xcan.angus.core.gm.interfaces.department.facade;

import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUserAddDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUserRemoveDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUserTransferDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentUserAddVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentUserTransferVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentUserVo;
import cloud.xcan.angus.remote.PageResult;

public interface DepartmentUserFacade {

  /**
   * 添加部门用户
   */
  DepartmentUserAddVo addUsers(Long departmentId, DepartmentUserAddDto dto);

  /**
   * 转移部门用户
   */
  DepartmentUserTransferVo transferUsers(Long departmentId, DepartmentUserTransferDto dto);

  /**
   * 移除部门用户
   */
  void removeUser(Long departmentId, Long userId);

  /**
   * 批量移除部门用户
   */
  void removeUsers(Long departmentId, DepartmentUserRemoveDto dto);

  /**
   * 分页查询部门用户列表
   */
  PageResult<DepartmentUserVo> listUsers(Long departmentId, DepartmentUserFindDto dto);

  /**
   * 分页查询未加入指定部门的用户列表
   */
  PageResult<DepartmentUserVo> listUsersNotInDepartment(Long departmentId,
      DepartmentUserFindDto dto);

}
