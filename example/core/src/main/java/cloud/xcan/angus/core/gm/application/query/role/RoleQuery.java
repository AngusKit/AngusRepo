package cloud.xcan.angus.core.gm.application.query.role;

import cloud.xcan.angus.api.commonlink.department.Department;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.role.Role;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface RoleQuery {

  /**
   * 根据ID查找角色并检查是否存在
   */
  Role findAndCheck(Long id);

  /**
   * 根据ID查找角色并检查是否存在
   */
  List<Role> findAndCheck(Collection<Long> ids);

  /**
   * 分页查找角色
   */
  Page<Role> find(GenericSpecification<Role> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * 查询租户所有自定义和系统定义角色
   */
  List<Role> findWideTenantRoles(Long tenantId);

  /**
   * 根据角色ID分页查找用户（包含部门和组的关联用户）
   */
  Page<User> findWideUsersByRoleId(Long roleId, String name, PageRequest pageable);

  /**
   * 根据角色ID分页查找用户（仅直接用户授权，不包含部门和组的关联用户）
   */
  Page<User> findUsersByRoleId(Long roleId, String name, PageRequest pageable);

  /**
   * 根据角色ID分页查找部门
   */
  Page<Department> findDepartmentsByRoleId(Long roleId, String name, PageRequest pageable);

  /**
   * 根据角色ID分页查找组
   */
  Page<Group> findGroupsByRoleId(Long roleId, String name, PageRequest pageable);

  /**
   * 查询用户所有角色（包含直接分配和通过部门、组分配的角色）
   */
  List<Role> findWideRolesByUserId(Long userId);

  /**
   * 统计指定应用的角色数量（系统角色也应该被包含）
   */
  long countRolesByApplicationId(Long id);

  /**
   * 查询应用下所有角色
   */
  List<Role> findByAppId(Long appId);

  /**
   * 查询应用下默认角色
   */
  Role findByAppCodeAndIsDefaultTrue(String appCodeStr, String editionType);

  /**
   * 查询应用下默认角色
   */
  Role findByAppIdAndIsDefaultTrue(Long appId);

  /**
   * 根据角色ID列表查询角色列表
   */
  List<Role> findAllById(Set<Long> roleIds);

  /**
   * 查询角色所有授权用户
   */
  List<User> findUsersByRoleId(Long roleId);

  /**
   * 统计角色总数
   */
  long countTotal();

  /**
   * 统计拥有角色的用户总数
   */
  long countTotalUsers();

  /**
   * 根据角色ID统计用户数
   */
  long countUsersByRoleId(Long roleId);

  /**
   * 批量根据角色ID统计用户数
   *
   * @param roleIds 角色ID列表
   * @return Map，key为角色ID，value为用户数
   */
  Map<Long, Long> countUsersByRoleIds(Collection<Long> roleIds);

  /**
   * 统计系统角色数
   */
  long countSystemRoles();

  /**
   * 统计自定义角色数
   */
  long countCustomRoles();

}
