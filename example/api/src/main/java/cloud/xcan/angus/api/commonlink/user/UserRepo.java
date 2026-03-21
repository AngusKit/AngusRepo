package cloud.xcan.angus.api.commonlink.user;

import cloud.xcan.angus.api.commonlink.user.enums.UserStatus;
import cloud.xcan.angus.core.jpa.entity.projection.IdAndName;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户仓储接口
 */
@Repository("commonUserRepo")
public interface UserRepo extends BaseRepository<User, Long> {

  /**
   * 根据邮箱查找用户
   */
  User findByEmail(String email);

  /**
   * 检查用户名是否存在
   */
  boolean existsByUsername(String username);

  /**
   * 检查用户名是否存在（排除指定ID）
   */
  boolean existsByUsernameAndIdNot(String username, Long id);

  /**
   * 检查邮箱是否存在
   */
  boolean existsByEmail(String email);

  /**
   * 检查手机号是否存在
   */
  boolean existsByPhone(String phone);

  /**
   * 根据用户名查找用户
   */
  User findByUsername(String username);

  /**
   * 统计指定部门的用户数量
   */
  long countByDepartmentId(Long departmentId);

  /**
   * 统计指定状态的用户数量
   */
  long countByStatus(UserStatus status);

  /**
   * 统计指定日期之后创建的用户数量
   */
  long countByCreatedDateAfter(LocalDateTime date);

  /**
   * 统计指定日期之前创建的用户数量（用于计算变化量）
   */
  long countByCreatedDateBefore(LocalDateTime date);

  /**
   * 统计在线用户数量（状态为ACTIVE且online为true）
   */
  @Query(value = "SELECT COUNT(*) FROM gm_user u "
      + "WHERE u.tenant_id = :tenantId AND u.status = 'ACTIVE' AND u.online = 1", nativeQuery = true)
  long countOnlineUsers(@Param("tenantId") Long tenantId);

  /**
   * 统计系统管理员数量
   */
  @Query(value = "SELECT COUNT(*) FROM gm_user u "
      + "WHERE u.tenant_id = :tenantId AND u.sys_admin = 1 AND u.status = 'ACTIVE'", nativeQuery = true)
  long countSysAdminUsers(@Param("tenantId") Long tenantId);

  /**
   * 统计指定日期之前创建的系统管理员数量（用于计算变化量）
   */
  @Query(value = "SELECT COUNT(*) FROM gm_user u "
      + "WHERE u.tenant_id = :tenantId AND u.sys_admin = 1 AND u.status = 'ACTIVE' AND u.created_date < :date", nativeQuery = true)
  long countSysAdminUsersByCreatedDateBefore(@Param("tenantId") Long tenantId,
      @Param("date") LocalDateTime date);

  /**
   * 按月份统计用户总数（用于增长趋势，返回过去N个月的数据） 返回格式：[{year, month, count}]
   */
  @Query(value = """
      SELECT YEAR(u.created_date) as year, MONTH(u.created_date) as month, COUNT(*) as count
      FROM gm_user u
      WHERE u.tenant_id = :tenantId AND u.created_date >= :startDate
      GROUP BY YEAR(u.created_date), MONTH(u.created_date)
      ORDER BY year, month""", nativeQuery = true)
  List<Object[]> countUsersByMonth(@Param("tenantId") Long tenantId,
      @Param("startDate") LocalDateTime startDate);

  /**
   * 按天统计用户创建日期（用于增长趋势，返回指定日期范围内的数据） 返回格式：[{date, count}]，date 格式为 YYYY-MM-DD
   */
  @Query(value = """
      SELECT DATE(u.created_date) as date, COUNT(*) as count
      FROM gm_user u
      WHERE u.tenant_id = :tenantId AND u.created_date >= :startDate AND u.created_date <= :endDate
      GROUP BY DATE(u.created_date)
      ORDER BY date""", nativeQuery = true)
  List<Object[]> countUsersByDay(@Param("tenantId") Long tenantId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  /**
   * 按周统计用户创建日期（用于增长趋势，返回指定日期范围内的数据） 返回格式：[{year, week, count}]，week 为一年中的第几周
   */
  @Query(value = """
      SELECT YEAR(u.created_date) as year, WEEK(u.created_date) as week, COUNT(*) as count
      FROM gm_user u
      WHERE u.tenant_id = :tenantId AND u.created_date >= :startDate AND u.created_date <= :endDate
      GROUP BY YEAR(u.created_date), WEEK(u.created_date)
      ORDER BY year, week""", nativeQuery = true)
  List<Object[]> countUsersByWeek(@Param("tenantId") Long tenantId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  /**
   * 统计指定租户ID列表的用户数量
   */
  @Query(value = "SELECT COUNT(*) FROM gm_user u "
      + "WHERE u.tenant_id IN :tenantIds", nativeQuery = true)
  long countByTenantIdIn(List<Long> tenantIds);

  /**
   * 统计指定租户ID列表下指定状态的用户数量
   */
  @Query(value = "SELECT COUNT(*) FROM gm_user u "
      + "WHERE u.tenant_id IN :tenantIds AND u.status = :status", nativeQuery = true)
  long countByTenantIdInAndStatus(@Param("tenantIds") List<Long> tenantIds,
      @Param("status") String status);

  /**
   * 统计指定租户ID列表的在线用户数量（状态为ACTIVE且online为true）
   */
  @Query(value = "SELECT COUNT(*) FROM gm_user u "
      + "WHERE u.tenant_id IN :tenantIds AND u.status = 'ACTIVE' AND u.online = 1", nativeQuery = true)
  long countOnlineUsersByTenantIdIn(@Param("tenantIds") List<Long> tenantIds);

  /**
   * 统计指定租户ID列表在指定日期之前创建的用户数量（用于计算变化量）
   */
  @Query(value = "SELECT COUNT(*) FROM gm_user u "
      + "WHERE u.tenant_id IN :tenantIds AND u.created_date < :date", nativeQuery = true)
  long countByCreatedDateBeforeAndTenantIdIn(@Param("tenantIds") List<Long> tenantIds,
      @Param("date") LocalDateTime date);

  /**
   * 按天统计指定租户ID列表的用户创建日期（用于增长趋势）
   */
  @Query(value = """
      SELECT DATE(u.created_date) as date, COUNT(*) as count
      FROM gm_user u
      WHERE u.tenant_id IN :tenantIds AND u.created_date >= :startDate AND u.created_date <= :endDate
      GROUP BY DATE(u.created_date)
      ORDER BY date""", nativeQuery = true)
  List<Object[]> countUsersByDayAndTenantIdIn(@Param("tenantIds") List<Long> tenantIds,
      @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

  /**
   * 按周统计指定租户ID列表的用户创建日期（用于增长趋势）
   */
  @Query(value = """
      SELECT YEAR(u.created_date) as year, WEEK(u.created_date) as week, COUNT(*) as count
      FROM gm_user u
      WHERE u.tenant_id IN :tenantIds AND u.created_date >= :startDate AND u.created_date <= :endDate
      GROUP BY YEAR(u.created_date), WEEK(u.created_date)
      ORDER BY year, week""", nativeQuery = true)
  List<Object[]> countUsersByWeekAndTenantIdIn(@Param("tenantIds") List<Long> tenantIds,
      @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

  /**
   * 按月份统计指定租户ID列表的用户总数（用于增长趋势）
   */
  @Query(value = """
      SELECT YEAR(u.created_date) as year, MONTH(u.created_date) as month, COUNT(*) as count
      FROM gm_user u
      WHERE u.tenant_id IN :tenantIds AND u.created_date >= :startDate
      GROUP BY YEAR(u.created_date), MONTH(u.created_date)
      ORDER BY year, month""", nativeQuery = true)
  List<Object[]> countUsersByMonthAndTenantIdIn(@Param("tenantIds") List<Long> tenantIds,
      @Param("startDate") LocalDateTime startDate);

  /**
   * 根据ID列表查找有效的用户ID列表（状态为ACTIVE）
   */
  @Query(value = "SELECT u.id FROM gm_user u "
      + "WHERE u.status = 'ACTIVE' AND u.id IN ?1", nativeQuery = true)
  List<Long> findIdsByIdIn(Collection<Long> ids);

  /**
   * 根据用户名列表查找有效的用户ID列表（状态为ACTIVE）
   */
  @Query(value = "SELECT u.id FROM gm_user u "
      + "WHERE u.status = 'ACTIVE' AND u.username IN ?1", nativeQuery = true)
  List<Long> findIdsByUsernameIn(Collection<String> usernames);

  /**
   * 根据在线状态查找用户名列表（状态为ACTIVE）
   */
  @Query(value = "SELECT u.username FROM gm_user u "
      + "WHERE u.status = 'ACTIVE' AND u.online = ?1", nativeQuery = true)
  List<String> findUsernamesByOnline(Boolean online);

  /**
   * 根据租户ID列表和在线状态查找用户ID列表（状态为ACTIVE）
   */
  @Query(value = "SELECT u.id FROM gm_user u "
      + "WHERE u.status = 'ACTIVE' AND u.tenant_id IN ?1 AND u.online = ?2", nativeQuery = true)
  List<Long> findIdsByTenantIdAndOnline(Collection<Long> tenantIds, Boolean online);

  /**
   * 根据租户ID列表和在线状态查找用户名列表（状态为ACTIVE）
   */
  @Query(value = "SELECT u.username FROM gm_user u "
      + "WHERE u.status = 'ACTIVE' AND u.tenant_id IN ?1 AND u.online = ?2", nativeQuery = true)
  List<String> findUsernamesByTenantIdAndOnline(Collection<Long> tenantIds, Boolean online);

  /**
   * 根据ID列表和在线状态查找用户名列表（状态为ACTIVE）
   */
  @Query(value = "SELECT u.username FROM gm_user u "
      + "WHERE u.status = 'ACTIVE' AND u.id IN ?1 AND u.online = ?2", nativeQuery = true)
  List<String> findUsernamesByIdAndOnline(Collection<Long> ids, Boolean online);

  /**
   * 根据邮箱查找租户ID
   */
  @Query(value = "SELECT u.tenant_id FROM gm_user u "
      + "WHERE u.email = ?1 LIMIT 1", nativeQuery = true)
  Long findTenantIdByEmail(String email);

  /**
   * 根据手机号查找租户ID
   */
  @Query(value = "SELECT u.tenant_id FROM gm_user u "
      + "WHERE u.phone = ?1 LIMIT 1", nativeQuery = true)
  Long findTenantIdByPhone(String phone);

  /**
   * 根据条件分页查询用户
   */
  @Override
  Page<User> findAll(Specification<User> spc, Pageable pageable);

  /**
   * 根据租户ID分页查询用户
   */
  Page<User> findAllByTenantId(Long tenantId, Pageable page);

  /**
   * 根据租户ID查询所有用户
   */
  List<User> findAllByTenantId(Long tenantId);

  /**
   * 根据租户ID查找有效用户列表（状态为ACTIVE、未锁定）
   */
  @Query(value = "SELECT * FROM gm_user u "
      + "WHERE u.tenant_id = ?1 AND u.status = 'ACTIVE' AND u.locked = 0", nativeQuery = true)
  List<User> findValidByTenantId(Long tenantId);

  /**
   * 根据租户ID查找有效的系统管理员用户列表（状态为ACTIVE、未锁定）
   */
  @Query(value = "SELECT * FROM gm_user u "
      + "WHERE u.tenant_id = ?1 AND u.sys_admin = 1 AND u.status = 'ACTIVE' AND u.locked = 0", nativeQuery = true)
  List<User> findValidSysAdminByTenantId(Long tenantId);

  /**
   * 根据租户ID查找有效的系统管理员用户ID列表（状态为ACTIVE、未锁定）
   */
  @Query(value = "SELECT u.id FROM gm_user u "
      + "WHERE u.tenant_id = ?1 AND u.sys_admin = 1 AND u.status = 'ACTIVE' AND u.locked = 0", nativeQuery = true)
  List<Long> findValidSysAdminIdsByTenantId(Long tenantId);

  /**
   * 根据租户ID和系统管理员标识查找用户列表
   */
  List<User> findByTenantIdAndSysAdmin(Long tenantId, boolean sysAdmin);

  /**
   * 根据用户名查找用户列表（排除指定ID）
   */
  List<User> findByUsernameAndIdNot(String username, Long id);

  /**
   * 根据手机号查找用户列表（排除指定ID）
   */
  List<User> findByPhoneAndIdNot(String phone, Long id);

  /**
   * 根据邮箱查找用户列表（排除指定ID）
   */
  List<User> findByEmailAndIdNot(String email, Long id);

  /**
   * 统计指定租户的用户数量
   */
  long countByTenantId(Long tenantId);

  /**
   * 查找所有用户ID（用于应用有默认角色时，所有用户均为应用用户）
   */
  @Query(value = "SELECT u.id FROM gm_user u", nativeQuery = true)
  List<Long> findAllIds();

  /**
   * 分页查找有效用户ID列表（已启用）
   */
  @Query(value = "SELECT u.id FROM gm_user u WHERE u.status = 'ACTIVE'", nativeQuery = true)
  Page<Long> findValidId(Pageable page);

  /**
   * 根据租户ID分页查找有效用户ID列表（已启用）
   */
  @Query(value = "SELECT u.id FROM gm_user u WHERE u.tenant_id = ?1 AND u.status = 'ACTIVE'", nativeQuery = true)
  Page<Long> findValidIdByTenantId(Long tenantId, Pageable page);

  /**
   * 根据租户ID列表分页查找有效用户邮箱列表（已启用）
   */
  @Query(value = "SELECT u.email FROM gm_user u WHERE u.tenant_id IN (?1) AND u.status = 'ACTIVE'", nativeQuery = true)
  Page<String> findValidEmailByTenantIdIn(Collection<?> tenantIds, Pageable page);

  /**
   * 根据租户ID分页查找有效用户邮箱列表（已启用）
   */
  @Query(value = "SELECT u.email FROM gm_user u WHERE u.tenant_id = ?1 AND u.status = 'ACTIVE'", nativeQuery = true)
  Page<String> findValidEmailByTenantId(Long tenantId, Pageable page);

  /**
   * 根据ID列表查找有效用户邮箱列表（已启用）
   */
  @Query(value = "SELECT u.email FROM gm_user u WHERE u.id IN (?1) AND u.status = 'ACTIVE'", nativeQuery = true)
  List<String> findValidEmailByIdIn(Collection<?> ids);

  /**
   * 分页查找所有有效用户邮箱列表（已启用）
   */
  @Query(value = "SELECT u.email FROM gm_user u WHERE u.status = 'ACTIVE'", nativeQuery = true)
  Page<String> findValidAllEmail(Pageable page);

  /**
   * 根据租户ID列表分页查找有效用户手机号列表（已启用）
   */
  @Query(value = "SELECT u.phone FROM gm_user u WHERE u.tenant_id IN (?1) AND u.status = 'ACTIVE'", nativeQuery = true)
  Page<String> findValidPhoneByTenantIdIn(Collection<?> tenantIds, Pageable page);

  /**
   * 根据租户ID分页查找有效用户手机号列表（已启用）
   */
  @Query(value = "SELECT u.phone FROM gm_user u WHERE u.tenant_id = ?1 AND u.status = 'ACTIVE'", nativeQuery = true)
  Page<String> findValidPhoneByTenantId(Long tenantId, Pageable page);

  /**
   * 根据ID列表查找有效用户手机号列表（已启用）
   */
  @Query(value = "SELECT u.phone FROM gm_user u WHERE u.id IN (?1) AND u.status = 'ACTIVE'", nativeQuery = true)
  List<String> findValidPhoneByIdIn(Collection<?> ids);

  /**
   * 分页查找所有有效用户手机号列表（已启用）
   */
  @Query(value = "SELECT u.phone FROM gm_user u WHERE u.status = 'ACTIVE'", nativeQuery = true)
  Page<String> findValidAllPhone(Pageable page);

  /**
   * 根据状态分页查询用户
   */
  Page<User> findAllByStatus(UserStatus status, Pageable page);

  /**
   * 根据状态和ID列表分页查询用户
   */
  Page<User> findAllByStatusAndIdIn(UserStatus status, Collection<?> ids, Pageable page);

  /**
   * 根据状态和ID列表（排除）分页查询用户
   */
  Page<User> findAllByStatusAndIdNotIn(UserStatus status, Collection<?> ids, Pageable page);

  /**
   * 根据状态和ID列表查询用户
   */
  List<User> findAllByStatusAndIdIn(UserStatus status, Collection<?> ids);

  /**
   * 根据手机号列表查询用户
   */
  List<User> findAllByPhoneIn(Set<String> phones);

  /**
   * 根据邮箱列表查询用户
   */
  List<User> findAllByEmailIn(Set<String> emails);

  /**
   * 根据手机号或邮箱查询用户
   */
  List<User> findByPhoneOrEmail(String phone, String email);

  /**
   * 根据手机号、邮箱或用户名查询用户
   */
  List<User> findByPhoneOrEmailOrUsername(String phone, String email, String username);

  /**
   * 根据手机号查询用户列表（需要关闭多租户控制）
   */
  List<User> findAllByEmail(String email);

  /**
   * 根据手机号查询用户列表（需要关闭多租户控制）
   */
  List<User> findAllByPhone(String phone);

  /**
   * 根据ID列表查找有效用户列表（状态为ACTIVE、未锁定）
   */
  @Query(value = "SELECT * FROM gm_user u "
      + "WHERE u.id IN (?1) AND u.status = 'ACTIVE' AND u.locked = 0", nativeQuery = true)
  List<User> findValidByIdIn(Collection<?> ids);

  /**
   * 根据ID查找有效用户（状态为ACTIVE、未锁定）
   */
  @Query(value = "SELECT * FROM gm_user u "
      + "WHERE u.id = ?1 AND u.status = 'ACTIVE' AND u.locked = 0", nativeQuery = true)
  Optional<User> findValidById(Long id);

  /**
   * 根据ID列表查找用户ID列表
   */
  @Query(value = "SELECT u.id FROM gm_user u WHERE u.id IN (?1)", nativeQuery = true)
  List<Long> findUserIdsByIdIn(Collection<?> ids);

  /**
   * 根据ID列表查找有效用户ID列表（状态为ACTIVE、未锁定）
   */
  @Query(value = "SELECT u.id FROM gm_user u WHERE u.id IN (?1)  AND u.status = 'ACTIVE' AND u.locked = 0", nativeQuery = true)
  List<Long> findValidUserIdsByIdIn(Collection<?> ids);

  /**
   * 根据用户ID查找组织ID列表（包括部门和群组）
   */
  @Query(value =
      "SELECT du.department_id as orgId FROM gm_department_user du WHERE du.user_id = ?1 "
          + "UNION SELECT gu.group_id as orgId FROM gm_group_user gu WHERE gu.user_id = ?1", nativeQuery = true)
  List<Long> findOrgIdsById(Long id);

  /**
   * 根据组织ID列表查找组织ID和名称列表（包括用户、部门和群组）
   */
  @Query(value = "SELECT id, name FROM gm_user WHERE id IN ?1 "
      + "UNION SELECT id, name FROM gm_department WHERE id IN ?1 "
      + "UNION SELECT id, name FROM gm_group WHERE id IN ?1", nativeQuery = true)
  List<IdAndName> findOrgIdAndNameByIds(Collection<?> orgIds);

  /**
   * 根据用户ID查找有效组织ID列表（包括部门和有效状态的群组）
   */
  @Query(value =
      "SELECT du.department_id as orgId FROM gm_department_user du, gm_department d "
          + "WHERE du.department_id = d.id AND du.user_id = ?1 AND d.status = 'ENABLED'"
          + "UNION SELECT gu.group_id as orgId FROM gm_group_user gu, gm_group g "
          + "WHERE gu.group_id = g.id AND gu.user_id = ?1 AND g.status = 'ENABLED'", nativeQuery = true)
  List<Long> findValidOrgIdsById(Long id);

  /**
   * 根据租户ID查找主系统管理员用户（状态为ACTIVE、未锁定）
   */
  @Query(value = "SELECT * FROM gm_user u WHERE u.sys_admin = 1 AND u.status = 'ACTIVE'"
      + " AND u.locked = 0  AND u.tenant_id = ?1 ORDER BY u.id ASC LIMIT 1", nativeQuery = true)
  Optional<User> findMainSysAdminUser(Long tenantId);

  /**
   * 根据租户ID查找所有系统管理员用户列表（状态为ACTIVE、未锁定）
   */
  @Query(value = "SELECT * FROM gm_user u WHERE u.sys_admin = 1 AND u.status = 'ACTIVE' "
      + "AND u.locked = 0 AND u.tenant_id = ?1", nativeQuery = true)
  List<User> findAllSysAdminUser(Long tenantId);

  /**
   * 根据租户ID查找系统管理员用户ID列表（状态为ACTIVE、未锁定）
   */
  @Query(value = "SELECT u.id FROM gm_user u WHERE u.sys_admin = 1 AND u.status = 'ACTIVE'  AND u.locked = 0 AND u.tenant_id = ?1", nativeQuery = true)
  List<Long> findIdsSysAdminUser(Long tenantId);

  /**
   * 统计指定租户的有效系统管理员用户数量（状态为ACTIVE、未锁定）
   */
  @Query(value = "SELECT count(*) FROM gm_user u WHERE u.sys_admin = 1 AND u.status = 'ACTIVE'  AND u.locked = 0 AND u.tenant_id = ?1", nativeQuery = true)
  int countValidSysAdminUser(Long tenantId);

  /**
   * 根据用户ID查找用户
   */
  @Query(value = "SELECT * FROM gm_user u WHERE u.id = ?1", nativeQuery = true)
  User findByUserId(Long id);

  /**
   * 查找活跃用户列表（按最后登录时间排序，状态为ACTIVE）
   */
  @Query(value = "SELECT * FROM gm_user u WHERE u.status = 'ACTIVE' ORDER BY u.id ASC LIMIT ?1", nativeQuery = true)
  List<User> findTopUsers(int num);

  /**
   * 根据名称模糊查询用户（状态为ACTIVE）
   */
  @Query(value = "SELECT * FROM gm_user u WHERE u.name LIKE CONCAT('%', ?1, '%') AND u.status = 'ACTIVE'", nativeQuery = true)
  List<User> findByNameLike(String name);

  /**
   * 查找锁定到期的用户ID列表（锁定开始时间已到且当前未锁定）
   */
  @Query(value = "SELECT * FROM gm_user WHERE lock_start_date <= ?1 AND locked = 0 AND (lock_end_date >= ?1 OR lock_start_date is NULL) LIMIT ?2", nativeQuery = true)
  Set<Long> findLockExpire(LocalDateTime now, Long count);

  /**
   * 查找解锁到期的用户ID列表（锁定结束时间已到且当前已锁定）
   */
  @Query(value = "SELECT * FROM gm_user WHERE lock_end_date <= ?1 AND locked = 1 LIMIT ?2", nativeQuery = true)
  Set<Long> findUnockExpire(LocalDateTime now, Long count);

  /**
   * 批量更新用户锁定状态为已锁定
   */
  @Modifying
  @Query(value = "update gm_user t set t.locked = 1, last_lock_date = NOW() WHERE t.id IN (?1)", nativeQuery = true)
  void updateLockStatusByIdIn(Collection<Long> userIds);

  /**
   * 批量更新用户锁定状态为未锁定
   */
  @Modifying
  @Query(value = "update gm_user t set t.locked = 0, lock_start_date = null, lock_end_date = null WHERE t.id IN (?1)", nativeQuery = true)
  void updateUnlockStatusByIdIn(Collection<Long> userIds);

  /**
   * 批量更新用户在线状态为在线 注意：移除 a.online = false 条件，确保即使用户已经在线，也能更新 onlineDate
   */
  @Modifying
  @Query("update User a set a.online = true, a.onlineDate = now() where a.id in ?1")
  void updateOnlineStatus(Collection<Long> userId);

  /**
   * 批量更新用户在线状态为离线
   */
  @Modifying
  @Query("update User a set a.online = false, a.offlineDate = now() where a.id in ?1 and a.online = true")
  void updateOfflineStatus(Collection<Long> userId);

  /**
   * 根据用户名更新用户离线状态
   */
  @Modifying
  @Query("update User a set a.online = false, a.offlineDate = now() where a.username = ?1 and a.online = true")
  void updateOfflineStatusByUsername(String username);

  /**
   * 批量更新用户主部门（从源部门更新为目标部门）
   */
  @Modifying
  @Query(value = "UPDATE gm_user SET department_id = ?1 WHERE id IN (?2) AND department_id = ?3", nativeQuery = true)
  void updateMainDepartmentByIdIn(Long targetDepartmentId, Collection<Long> userIds,
      Long sourceDepartmentId);


  /**
   * 批量清除用户主部门（清除指定用户列表中主部门为指定部门的用户的主部门）
   */
  @Modifying
  @Query(value = "UPDATE gm_user SET department_id = NULL WHERE id IN (?1) AND department_id = ?2", nativeQuery = true)
  void clearMainDepartmentByIdIn(Collection<Long> userIds, Long departmentId);

  /**
   * 批量清除用户主部门（清除指定部门下所有用户的主部门）
   */
  @Modifying
  @Query(value = "UPDATE gm_user SET department_id = NULL WHERE department_id = ?1", nativeQuery = true)
  void clearMainDepartmentByDepartmentId(Long departmentId);

  /**
   * 根据租户ID列表软删除用户（清空手机号、邮箱、用户名，标记为已删除）
   */
  @Modifying
  void deleteByTenantId(Long id);

}
