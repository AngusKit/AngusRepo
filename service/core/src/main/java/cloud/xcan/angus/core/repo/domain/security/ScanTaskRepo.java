package cloud.xcan.angus.core.repo.domain.security;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface ScanTaskRepo extends BaseRepository<ScanTask, String> {

    Optional<ScanTask> findByTenantIdAndId(String tenantId, String id);

    List<ScanTask> findByTenantIdAndArtifactId(String tenantId, String artifactId);

    List<ScanTask> findByTenantIdAndRepositoryId(String tenantId, String repositoryId);

    List<ScanTask> findByTenantIdAndStatus(String tenantId, ScanStatus status);

    @Query("SELECT COUNT(st) FROM ScanTask st WHERE st.tenantId = :tenantId")
    Long countTotalScans(@Param("tenantId") String tenantId);

    @Query("SELECT COUNT(st) FROM ScanTask st WHERE st.tenantId = :tenantId AND st.status = :status")
    Long countByStatus(@Param("tenantId") String tenantId, @Param("status") ScanStatus status);

    @Query("SELECT COALESCE(SUM(st.vulnerabilityCount), 0) FROM ScanTask st WHERE st.tenantId = :tenantId AND st.status = cloud.xcan.angus.core.repo.domain.security.ScanStatus.COMPLETED")
    Long sumVulnerabilities(@Param("tenantId") String tenantId);
}
