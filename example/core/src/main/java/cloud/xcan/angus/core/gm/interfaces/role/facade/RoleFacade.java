package cloud.xcan.angus.core.gm.interfaces.role.facade;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.api.gm.RolePermissionVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleCreateDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleDefaultDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleFindDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleObjectFindDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RolePermissionUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.AuthorizableApplicationMenuVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.RoleDefaultVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.RoleDetailVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.RoleListVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.RoleStatsVo;
import cloud.xcan.angus.api.gm.user.vo.UserListVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

public interface RoleFacade {

  /**
   * 创建角色
   */
  RoleDetailVo create(RoleCreateDto dto);

  /**
   * 更新角色
   */
  RoleDetailVo update(Long id, RoleUpdateDto dto);

  /**
   * 更新角色状态
   */
  RoleDetailVo updateStatus(Long id, EnabledStatusUpdateDto dto);

  /**
   * 删除角色
   */
  void delete(Long id);

  /**
   * 获取角色详情
   */
  RoleDetailVo getDetail(Long id);

  /**
   * 获取角色列表（支持分页）
   */
  PageResult<RoleListVo> list(RoleFindDto dto);

  /**
   * 获取角色列表
   */
  List<RoleListVo> list(List<Long> ids);

  /**
   * 获取角色统计数据
   */
  RoleStatsVo getStats();

  /**
   * 获取角色权限配置
   */
  RolePermissionVo getPermissions(Long id);

  /**
   * 更新角色权限配置
   */
  RolePermissionVo updatePermissions(Long id, RolePermissionUpdateDto dto);

  /**
   * 设置角色为默认角色
   */
  RoleDefaultVo setDefault(Long id, RoleDefaultDto dto);

  /**
   * 获取角色的用户列表（包含部门和组的关联用户）
   */
  PageResult<UserListVo> getWideUsers(Long id, RoleObjectFindDto dto);

  /**
   * 获取角色的用户列表（仅直接用户授权，不包含部门和组的关联用户）
   */
  PageResult<UserListVo> getUsers(Long id, RoleObjectFindDto dto);

  /**
   * 获取角色的部门列表
   */
  PageResult<DepartmentDetailVo> getDepartments(Long id, RoleObjectFindDto dto);

  /**
   * 获取角色的组列表
   */
  PageResult<GroupDetailVo> getGroups(Long id, RoleObjectFindDto dto);

  /**
   * 根据角色ID查询可授权应用菜单树
   *
   * @param roleId 角色ID
   * @return 可授权应用菜单树，已授权菜单标记为 authorized=true
   */
  List<AuthorizableApplicationMenuVo> getAuthorizableMenus(Long roleId);

}
