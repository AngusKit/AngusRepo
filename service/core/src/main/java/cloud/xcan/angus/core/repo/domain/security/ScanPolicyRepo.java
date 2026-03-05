package cloud.xcan.angus.core.repo.domain.security;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface ScanPolicyRepo extends BaseRepository<ScanPolicy, String> {

    Optional<ScanPolicy> findByTenantIdAndId(String tenantId, String id);

    List<ScanPolicy> findByTenantIdAndRepositoryId(String tenantId, String repositoryId);

    Optional<ScanPolicy> findByTenantIdAndNameAndRepositoryId(String tenantId, String name, String repositoryId);

    boolean existsByTenantIdAndNameAndRepositoryIdAndIdNot(String tenantId, String name, String repositoryId, String id);

    @Query("SELECT COUNT(sp) FROM ScanPolicy sp WHERE sp.tenantId = :tenantId AND sp.enabled = true")
    Long countEnabledPolicies(@Param("tenantId") String tenantId);

    @Query("SELECT COUNT(sp) FROM ScanPolicy sp WHERE sp.tenantId = :tenantId")
    Long countTotalPolicies(@Param("tenantId") String tenantId);

    @Modifying
    @Query("UPDATE ScanPolicy sp SET sp.enabled = :enabled, sp.modifiedDate = :modifiedDate, sp.modifiedBy = :modifiedBy " +
           "WHERE sp.tenantId = :tenantId AND sp.id = :policyId")
    void updateEnabled(@Param("tenantId") String tenantId,
                      @Param("policyId") String policyId,
                      @Param("enabled") Boolean enabled,
                      @Param("modifiedDate") LocalDateTime modifiedDate,
                      @Param("modifiedBy") Long modifiedBy);
}
