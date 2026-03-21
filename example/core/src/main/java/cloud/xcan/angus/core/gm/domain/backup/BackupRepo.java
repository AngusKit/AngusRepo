package cloud.xcan.angus.core.gm.domain.backup;

import cloud.xcan.angus.core.gm.domain.backup.enums.BackupStatus;
import cloud.xcan.angus.core.gm.domain.backup.enums.BackupType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BackupRepo extends BaseRepository<Backup, Long> {

  Optional<Backup> findByName(String name);

  List<Backup> findByType(BackupType type);

  List<Backup> findByStatus(BackupStatus status);

  long countByType(BackupType type);

  long countByStatus(BackupStatus status);
}
