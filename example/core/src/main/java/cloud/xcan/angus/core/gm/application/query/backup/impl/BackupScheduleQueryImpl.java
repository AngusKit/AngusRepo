package cloud.xcan.angus.core.gm.application.query.backup.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.backup.BackupScheduleQuery;
import cloud.xcan.angus.core.gm.domain.backup.BackupSchedule;
import cloud.xcan.angus.core.gm.domain.backup.BackupScheduleRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BackupScheduleQueryImpl implements BackupScheduleQuery {

  @Resource
  private BackupScheduleRepo scheduleRepo;

  @Override
  public BackupSchedule findAndCheck(Long id) {
    return new BizTemplate<BackupSchedule>() {
      @Override
      protected BackupSchedule process() {
        return scheduleRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("备份计划「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public boolean existsByName(String name) {
    return new BizTemplate<Boolean>() {
      @Override
      protected Boolean process() {
        return scheduleRepo.findByName(name).isPresent();
      }
    }.execute();
  }

  @Override
  public List<BackupSchedule> findAll() {
    return new BizTemplate<List<BackupSchedule>>() {
      @Override
      protected List<BackupSchedule> process() {
        return scheduleRepo.findAll();
      }
    }.execute();
  }
}
