package cloud.xcan.angus.api.commonlink.department;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 部门用户关系仓储接口
 */
@Repository("commonDepartmentUserRepo")
public interface DepartmentUserRepo extends BaseRepository<DepartmentUser, Long> {

  /**
   * 根据部门ID查找部门用户关系列表
   */
  List<DepartmentUser> findByDepartmentId(Long departmentId);

  /**
   * 根据用户ID查找部门用户关系列表
   */
  List<DepartmentUser> findByUserId(Long userId);

  /**
   * 根据用户ID列表批量查找部门用户关系列表
   */
  List<DepartmentUser> findAllByUserIdIn(Collection<Long> userIds);

  /**
   * 根据部门ID和用户ID查找部门用户关系
   */
  Optional<DepartmentUser> findByDepartmentIdAndUserId(Long departmentId, Long userId);

  /**
   * 检查部门用户关系是否存在
   */
  boolean existsByDepartmentIdAndUserId(Long departmentId, Long userId);

  /**
   * 查找用户的主部门关系
   */
  Optional<DepartmentUser> findByUserIdAndIsPrimaryTrue(Long userId);

  /**
   * 根据部门ID列表查找有效用户的邮箱列表（分页）
   */
  @Query(value =
      "SELECT u.email FROM gm_user u INNER JOIN gm_department_user du ON du.user_id = u.id "
          + "AND du.department_id IN (?1) AND u.deleted = 0 AND u.status = 'ACTIVE' ORDER BY u.id ASC LIMIT ?2,?3", nativeQuery = true)
  List<String> findValidEmailByDeptIds(Collection<Long> deptIds, int offset, int size);

  /**
   * 根据部门ID列表查找有效用户的手机号列表（分页）
   */
  @Query(value =
      "SELECT u.mobile FROM gm_user u INNER JOIN gm_department_user du ON du.user_id = u.id "
          + "AND du.department_id IN (?1) AND u.deleted = 0 AND u.status = 'ACTIVE' ORDER BY u.id ASC LIMIT ?2,?3", nativeQuery = true)
  List<String> findValidMobileByDeptIds(Collection<Long> deptIds, int offset, int size);

  /**
   * 根据部门ID列表查找有效用户ID集合
   */
  @Query(value =
      "SELECT u.id FROM gm_department_user du INNER JOIN gm_user u ON du.user_id = u.id "
          + "AND du.department_id IN (?1) AND u.deleted = 0 AND u.status = 'ACTIVE'", nativeQuery = true)
  Set<Long> findValidUserIdsByDeptIds(Collection<Long> deptIds);

  /**
   * 根据部门ID列表和在线状态查找用户名集合
   */
  @Query(value =
      "SELECT u.username FROM gm_department_user du INNER JOIN gm_user u ON du.user_id = u.id "
          + "AND du.department_id IN (?1) AND u.deleted = 0 AND u.status = 'ACTIVE' AND u.online = ?2", nativeQuery = true)
  Set<String> findUsernamesByDeptIdInAndOnline(Collection<Long> deptIds, Boolean online);

  /**
   * 根据租户ID和部门ID列表查找有效用户ID集合
   */
  @Query(value =
      "SELECT u.id FROM gm_department_user du INNER JOIN gm_user u ON du.user_id = u.id "
          + "AND du.tenant_id = ?1 AND u.tenant_id = ?1 AND du.department_id IN (?2) "
          + "AND u.deleted = 0 AND u.status = 'ACTIVE'", nativeQuery = true)
  Set<Long> findValidUserIdsByTenantIdAndDeptIds(Long tenantId, Collection<Long> deptIds);

  /**
   * 根据部门ID列表查找用户ID集合
   */
  @Query(value = "SELECT du.user_id FROM gm_department_user du WHERE du.department_id IN (?1) ", nativeQuery = true)
  Set<Long> findUserIdsByDeptIds(Collection<Long> deptIds);

  /**
   * 根据部门ID列表分组统计用户数量
   */
  @Query(value = "SELECT du.department_id, COUNT(du.user_id) as user_count FROM gm_department_user du WHERE du.department_id IN (?1) GROUP BY du.department_id", nativeQuery = true)
  List<Object[]> countGroupByDepartmentIds(Collection<Long> departmentIds);

  /**
   * 根据部门ID删除所有部门用户关系
   */
  @Modifying
  void deleteByDepartmentId(Long departmentId);

  /**
   * 根据部门ID和用户ID删除部门用户关系
   */
  @Modifying
  void deleteByDepartmentIdAndUserId(Long departmentId, Long userId);

  /**
   * 根据部门ID和用户ID列表批量删除部门用户关系
   */
  @Modifying
  @Query("DELETE FROM DepartmentUser du WHERE du.departmentId = ?1 AND du.userId IN ?2")
  void deleteByDepartmentIdAndUserIdIn(Long departmentId, List<Long> userIds);

  /**
   * 根据用户ID列表批量删除部门用户关系
   */
  @Modifying
  void deleteAllByUserIdIn(Collection<Long> ids);

  /**
   * 根据租户ID删除所有部门用户关系
   */
  @Modifying
  void deleteByTenantId(Long tenantId);

}
