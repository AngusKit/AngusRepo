package cloud.xcan.angus.core.repo.domain.cleanup;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

/**
 * 清理执行记录仓储接口
 */
@NoRepositoryBean
public interface CleanupExecutionRepo extends BaseRepository<CleanupExecution, String> {

    // 基础查询方法
    Optional<CleanupExecution> findByTenantIdAndId(String tenantId, String id);
    
    List<CleanupExecution> findByTenantIdAndPolicyIdOrderByCreatedDateDesc(String tenantId, String policyId);
    
    Page<CleanupExecution> findByTenantIdAndPolicyIdOrderByCreatedDateDesc(String tenantId, String policyId, Pageable pageable);
    
    // 状态查询
    List<CleanupExecution> findByTenantIdAndStatus(String tenantId, CleanupStatus status);
    
    List<CleanupExecution> findByTenantIdAndPolicyIdAndStatus(String tenantId, String policyId, CleanupStatus status);
    
    boolean existsByTenantIdAndPolicyIdAndStatusIn(String tenantId, String policyId, List<CleanupStatus> statuses);

    // 统计查询
    @Query("SELECT COUNT(ce) FROM CleanupExecution ce WHERE ce.tenantId = :tenantId")
    Long countTotalExecutions(@Param("tenantId") String tenantId);

    @Query("SELECT COUNT(ce) FROM CleanupExecution ce WHERE ce.tenantId = :tenantId AND ce.status = :status")
    Long countByStatus(@Param("tenantId") String tenantId, @Param("status") CleanupStatus status);

    // 统计删除的制品总数（从JSON字段中提取）
    @Query(value = "SELECT COALESCE(SUM(CAST(JSON_EXTRACT(ce.statistics, '$.deletedArtifacts') AS UNSIGNED)), 0) " +
                   "FROM cleanup_execution ce WHERE ce.tenant_id = :tenantId AND ce.status = 'COMPLETED'", 
           nativeQuery = true)
    Long sumDeletedArtifacts(@Param("tenantId") String tenantId);

    // 统计释放的存储空间总数（从JSON字段中提取）
    @Query(value = "SELECT COALESCE(SUM(CAST(JSON_EXTRACT(ce.statistics, '$.freedSpaceBytes') AS UNSIGNED)), 0) " +
                   "FROM cleanup_execution ce WHERE ce.tenant_id = :tenantId AND ce.status = 'COMPLETED'", 
           nativeQuery = true)
    Long sumFreedSpaceBytes(@Param("tenantId") String tenantId);

    // 按策略统计执行次数
    @Query("SELECT COUNT(ce) FROM CleanupExecution ce WHERE ce.tenantId = :tenantId AND ce.policyId = :policyId")
    Long countByPolicy(@Param("tenantId") String tenantId, @Param("policyId") String policyId);

    // 获取最近的执行记录
    @Query("SELECT ce FROM CleanupExecution ce WHERE ce.tenantId = :tenantId AND ce.policyId = :policyId " +
           "ORDER BY ce.createdDate DESC")
    List<CleanupExecution> findRecentExecutions(@Param("tenantId") String tenantId, 
                                              @Param("policyId") String policyId, 
                                              Pageable pageable);

    // 清理历史数据
    @Modifying
    @Query("DELETE FROM CleanupExecution ce WHERE ce.tenantId = :tenantId AND ce.createdDate < :cutoffDate")
    void deleteOldExecutions(@Param("tenantId") String tenantId, @Param("cutoffDate") LocalDateTime cutoffDate);

    // 按时间范围查询执行记录（用于趋势分析）
    @Query("SELECT ce FROM CleanupExecution ce WHERE ce.tenantId = :tenantId " +
           "AND ce.createdDate >= :startDate AND ce.createdDate <= :endDate " +
           "ORDER BY ce.createdDate DESC")
    List<CleanupExecution> findByDateRange(@Param("tenantId") String tenantId,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
}