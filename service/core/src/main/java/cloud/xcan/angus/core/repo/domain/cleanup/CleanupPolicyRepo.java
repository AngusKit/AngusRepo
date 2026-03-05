package cloud.xcan.angus.core.repo.domain.cleanup;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

/**
 * 清理策略仓储接口
 */
@NoRepositoryBean
public interface CleanupPolicyRepo extends BaseRepository<CleanupPolicy, String> {

    // 基础查询方法
    Optional<CleanupPolicy> findByTenantIdAndId(String tenantId, String id);
    
    List<CleanupPolicy> findByTenantIdAndRepositoryId(String tenantId, String repositoryId);
    
    List<CleanupPolicy> findByTenantIdAndEnabled(String tenantId, Boolean enabled);
    
    Optional<CleanupPolicy> findByTenantIdAndNameAndRepositoryId(String tenantId, String name, String repositoryId);
    
    boolean existsByTenantIdAndNameAndRepositoryIdAndIdNot(String tenantId, String name, String repositoryId, String id);

    // 调度相关查询
    @Query("SELECT cp FROM CleanupPolicy cp WHERE cp.tenantId = :tenantId AND cp.enabled = true " +
           "AND cp.nextExecution IS NOT NULL AND cp.nextExecution <= :now")
    List<CleanupPolicy> findPendingExecutions(@Param("tenantId") String tenantId, @Param("now") LocalDateTime now);

    // 统计查询
    @Query("SELECT COUNT(cp) FROM CleanupPolicy cp WHERE cp.tenantId = :tenantId AND cp.enabled = true")
    Long countEnabledPolicies(@Param("tenantId") String tenantId);

    @Query("SELECT COUNT(cp) FROM CleanupPolicy cp WHERE cp.tenantId = :tenantId")
    Long countTotalPolicies(@Param("tenantId") String tenantId);

    // 按类型统计
    @Query("SELECT COUNT(cp) FROM CleanupPolicy cp WHERE cp.tenantId = :tenantId AND cp.type = :type")
    Long countByType(@Param("tenantId") String tenantId, @Param("type") CleanupType type);

    // 批量更新下次执行时间
    @Modifying
    @Query("UPDATE CleanupPolicy cp SET cp.nextExecution = :nextExecution " +
           "WHERE cp.tenantId = :tenantId AND cp.id = :policyId")
    void updateNextExecution(@Param("tenantId") String tenantId, 
                           @Param("policyId") String policyId, 
                           @Param("nextExecution") LocalDateTime nextExecution);

    // 批量更新执行统计
    @Modifying
    @Query("UPDATE CleanupPolicy cp SET cp.lastExecuted = :lastExecuted, " +
           "cp.executionCount = cp.executionCount + 1, " +
           "cp.lastExecutionStatsJson = :statsJson WHERE cp.tenantId = :tenantId AND cp.id = :policyId")
    void updateExecutionStats(@Param("tenantId") String tenantId,
                            @Param("policyId") String policyId,
                            @Param("lastExecuted") LocalDateTime lastExecuted,
                            @Param("statsJson") String statsJson);

    // 批量更新启用状态
    @Modifying
    @Query("UPDATE CleanupPolicy cp SET cp.enabled = :enabled, cp.modifiedDate = :modifiedDate, cp.modifiedBy = :modifiedBy " +
           "WHERE cp.tenantId = :tenantId AND cp.id = :policyId")
    void updateEnabled(@Param("tenantId") String tenantId,
                      @Param("policyId") String policyId,
                      @Param("enabled") Boolean enabled,
                      @Param("modifiedDate") LocalDateTime modifiedDate,
                      @Param("modifiedBy") Long modifiedBy);
}