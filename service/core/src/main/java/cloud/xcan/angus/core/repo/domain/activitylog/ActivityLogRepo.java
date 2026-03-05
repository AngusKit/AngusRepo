package cloud.xcan.angus.core.repo.domain.activitylog;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ActivityLogRepo extends BaseRepository<ActivityLog, String> {

  Optional<ActivityLog> findByTenantIdAndId(String tenantId, String id);

  List<ActivityLog> findByTenantIdAndUser(String tenantId, String user);

  List<ActivityLog> findByTenantIdAndRepository(String tenantId, String repository);

  List<ActivityLog> findByTenantIdAndCategory(String tenantId, ActivityCategory category);

  List<ActivityLog> findByTenantIdAndTimestampBefore(String tenantId, LocalDateTime beforeDate);

  long countByTenantId(String tenantId);

  long countByTenantIdAndTimestampBetween(String tenantId, LocalDateTime start, LocalDateTime end);
}
