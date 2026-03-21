package cloud.xcan.angus.core.gm.application.query.backup.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.backup.BackupRestoreTaskQuery;
import cloud.xcan.angus.core.gm.domain.backup.RestoreTask;
import cloud.xcan.angus.core.gm.domain.backup.RestoreTaskRepo;
import cloud.xcan.angus.core.gm.domain.backup.enums.RestoreStatus;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class BackupRestoreTaskQueryImpl implements BackupRestoreTaskQuery {

  @Resource
  private RestoreTaskRepo restoreTaskRepo;

  @Override
  public RestoreTask findAndCheck(Long id) {
    return new BizTemplate<RestoreTask>() {
      @Override
      protected RestoreTask process() {
        return restoreTaskRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("恢复任务「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Page<RestoreTask> find(GenericSpecification<RestoreTask> spec, PageRequest pageable) {
    return new BizTemplate<Page<RestoreTask>>() {
      @Override
      protected Page<RestoreTask> process() {
        return restoreTaskRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public List<RestoreTask> findByStatus(RestoreStatus status) {
    return new BizTemplate<List<RestoreTask>>() {
      @Override
      protected List<RestoreTask> process() {
        return restoreTaskRepo.findByStatus(status);
      }
    }.execute();
  }

  @Override
  public List<RestoreTask> findByBackupId(Long id) {
    return restoreTaskRepo.findByBackupId(id);
  }

}
