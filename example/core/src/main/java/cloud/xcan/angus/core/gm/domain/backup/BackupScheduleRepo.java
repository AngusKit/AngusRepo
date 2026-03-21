package cloud.xcan.angus.core.gm.domain.backup;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BackupScheduleRepo extends BaseRepository<BackupSchedule, Long> {

  Optional<BackupSchedule> findByName(String name);

  List<BackupSchedule> findByStatus(EnabledStatus status);

  List<BackupSchedule> findAll();
}
