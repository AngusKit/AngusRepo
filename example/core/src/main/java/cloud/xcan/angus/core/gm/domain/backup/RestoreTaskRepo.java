package cloud.xcan.angus.core.gm.domain.backup;

import cloud.xcan.angus.core.gm.domain.backup.enums.RestoreStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RestoreTaskRepo extends BaseRepository<RestoreTask, Long> {

  /**
   * 根据状态查询恢复任务列表
   */
  List<RestoreTask> findByStatus(RestoreStatus status);

  /**
   * 根据备份ID查询恢复任务列表
   */
  List<RestoreTask> findByBackupId(Long backupId);

}
