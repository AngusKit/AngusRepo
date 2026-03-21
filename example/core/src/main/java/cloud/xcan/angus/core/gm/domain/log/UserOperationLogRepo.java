package cloud.xcan.angus.core.gm.domain.log;

import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.log.enums.ResponseStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

/**
 * 用户操作日志仓储接口
 */
@NoRepositoryBean
public interface UserOperationLogRepo extends BaseRepository<UserOperationLog, Long> {

  /**
   * 根据用户ID查询日志
   */
  List<UserOperationLog> findByUserId(Long userId);

  /**
   * 统计各操作类型的数量
   */
  @Query("SELECT u.action, COUNT(u) FROM UserOperationLog u " +
      "WHERE u.createdDate BETWEEN :startDate AND :endDate " +
      "GROUP BY u.action")
  List<Object[]> countByActionAndDateRange(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  /**
   * 统计各资源类型的数量
   */
  @Query("SELECT u.resourceType, COUNT(u) FROM UserOperationLog u " +
      "WHERE u.createdDate BETWEEN :startDate AND :endDate " +
      "GROUP BY u.resourceType")
  List<Object[]> countByResourceTypeAndDateRange(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  /**
   * 统计指定资源类型在指定时间范围内的数量
   */
  @Query("SELECT COUNT(u) FROM UserOperationLog u " +
      "WHERE u.createdDate BETWEEN :startDate AND :endDate " +
      "AND u.resourceType = :resourceType")
  long countByResourceTypeAndDateRangeSingle(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("resourceType") ResourceType resourceType);

  /**
   * 查询操作最频繁的用户TOP N
   */
  @Query("SELECT u.userId, u.userName, COUNT(u) as operationCount FROM UserOperationLog u " +
      "WHERE u.createdDate BETWEEN :startDate AND :endDate " +
      "GROUP BY u.userId, u.userName " +
      "ORDER BY operationCount DESC")
  List<Object[]> findTopUsersByOperationCount(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  /**
   * 统计指定时间范围内的总操作次数
   */
  @Query("SELECT COUNT(u) FROM UserOperationLog u " +
      "WHERE u.createdDate BETWEEN :startDate AND :endDate")
  long countByDateRange(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  /**
   * 统计指定时间范围内的成功操作次数
   */
  @Query("SELECT COUNT(u) FROM UserOperationLog u " +
      "WHERE u.createdDate BETWEEN :startDate AND :endDate " +
      "AND u.responseStatus = :responseStatus")
  long countByResponseStatusAndDateRange(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("responseStatus") ResponseStatus responseStatus);

  /**
   * 删除指定时间范围内的日志
   */
  @Modifying
  @Query("DELETE FROM UserOperationLog u " +
      "WHERE u.createdDate < :beforeDate")
  int deleteByCreatedDateBefore(
      @Param("beforeDate") LocalDateTime beforeDate);

  /**
   * 统计指定租户ID列表的操作日志总数
   */
  @Query(value = "SELECT COUNT(*) FROM gm_user_operation_log u WHERE u.tenant_id IN :tenantIds", nativeQuery = true)
  long countByTenantIdIn(@Param("tenantIds") List<Long> tenantIds);

  /**
   * 统计指定租户ID列表在指定时间范围内的总操作次数
   */
  @Query(value = "SELECT COUNT(*) FROM gm_user_operation_log u " +
      "WHERE u.tenant_id IN :tenantIds AND u.created_date BETWEEN :startDate AND :endDate", nativeQuery = true)
  long countByTenantIdInAndDateRange(
      @Param("tenantIds") List<Long> tenantIds,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  /**
   * 统计指定租户ID列表在指定时间范围内的成功/失败操作次数
   */
  @Query(value = "SELECT COUNT(*) FROM gm_user_operation_log u " +
      "WHERE u.tenant_id IN :tenantIds AND u.created_date BETWEEN :startDate AND :endDate " +
      "AND u.response_status = :responseStatus", nativeQuery = true)
  long countByTenantIdInAndResponseStatusAndDateRange(
      @Param("tenantIds") List<Long> tenantIds,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("responseStatus") ResponseStatus responseStatus);

  /**
   * 统计指定租户ID列表指定资源类型在指定时间范围内的数量
   */
  @Query(value = "SELECT COUNT(*) FROM gm_user_operation_log u " +
      "WHERE u.tenant_id IN :tenantIds AND u.created_date BETWEEN :startDate AND :endDate " +
      "AND u.resource_type = :resourceType", nativeQuery = true)
  long countByTenantIdInAndResourceTypeAndDateRange(
      @Param("tenantIds") List<Long> tenantIds,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("resourceType") ResourceType resourceType);

  /**
   * 查询指定租户ID列表最近的日志，按创建时间倒序排列
   */
  @Query("SELECT u FROM UserOperationLog u " +
      "WHERE u.tenantId IN :tenantIds " +
      "AND (:resourceType IS NULL OR u.resourceType = :resourceType) " +
      "ORDER BY u.createdDate DESC")
  List<UserOperationLog> findRecentLogsByTenantIdIn(
      @Param("tenantIds") List<Long> tenantIds,
      @Param("resourceType") ResourceType resourceType,
      org.springframework.data.domain.Pageable pageable);
}
