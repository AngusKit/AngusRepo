package cloud.xcan.angus.core.gm.application.query.backup;

import cloud.xcan.angus.core.gm.domain.backup.RestoreTask;
import cloud.xcan.angus.core.gm.domain.backup.enums.RestoreStatus;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface BackupRestoreTaskQuery {

  /**
   * 查询并校验恢复任务存在性
   */
  RestoreTask findAndCheck(Long id);

  /**
   * 分页查询恢复任务列表
   */
  Page<RestoreTask> find(GenericSpecification<RestoreTask> spec, PageRequest pageable);

  /**
   * 根据状态查询恢复任务列表
   */
  List<RestoreTask> findByStatus(RestoreStatus status);

  /**
   * 查询恢复任务列表
   */
  List<RestoreTask> findByBackupId(Long id);

}
