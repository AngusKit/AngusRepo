package cloud.xcan.angus.core.gm.application.query.department;

import cloud.xcan.angus.api.commonlink.department.DepartmentUser;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 部门用户查询服务接口
 */
public interface DepartmentUserQuery {

  /**
   * 根据ID查找部门用户关系并检查是否存在
   */
  DepartmentUser findAndCheck(Long id);

  /**
   * 根据部门ID分页查询用户列表
   */
  Page<User> findUsers(Long departmentId, GenericSpecification<User> spec,
      PageRequest pageable);

  /**
   * 分页查询未加入指定部门的用户列表
   */
  Page<User> findUsersNotInDepartment(Long departmentId, GenericSpecification<User> spec,
      PageRequest pageable);

  /**
   * 根据部门ID列表统计每个部门的用户数量
   */
  Map<Long, Long> countUsersByDepartmentIds(Collection<Long> departmentIds);

  /**
   * 检查部门ID和用户ID的关系是否存在
   */
  boolean existsByDepartmentIdAndUserId(Long departmentId, Long userId);

  /**
   * 根据部门ID和用户ID查找关系
   */
  Optional<DepartmentUser> findByDepartmentIdAndUserId(Long departmentId, Long userId);

  /**
   * 根据部门ID查找所有用户关系
   */
  List<DepartmentUser> findByDepartmentId(Long departmentId);

  /**
   * 根据用户ID查找所有部门关系
   */
  List<DepartmentUser> findByUserId(Long userId);

  /**
   * 查找用户的主部门关系
   */
  Optional<DepartmentUser> findPrimaryByUserId(Long userId);

  /**
   * 根据用户ID列表批量查找部门用户关系
   */
  List<DepartmentUser> findByUserIdIn(List<Long> userIds);

}
