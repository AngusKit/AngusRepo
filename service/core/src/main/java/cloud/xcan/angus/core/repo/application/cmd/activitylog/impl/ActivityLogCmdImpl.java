package cloud.xcan.angus.core.repo.application.cmd.activitylog.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.activitylog.ActivityLogCmd;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityCategory;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLog;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLogRepo;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 活动日志命令实现
 */
@Biz
public class ActivityLogCmdImpl extends CommCmd<ActivityLog, String> implements ActivityLogCmd {

  @Autowired(required = false)
  private ActivityLogRepo activityLogRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ActivityLog create(ActivityLog activityLog) {
    // Null input returns null by design: this method may be called by internal batch
    // processing where null entries should be silently skipped rather than throwing exceptions
    if (Objects.isNull(activityLog)) {
      return null;
    }
    insert0(activityLog);
    return activityLog;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    if (!isUserAction() || Objects.isNull(id)) {
      return;
    }
    activityLogRepo.deleteById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteBatch(List<String> ids) {
    if (!isUserAction() || isEmpty(ids)) {
      return;
    }
    activityLogRepo.deleteAllById(ids);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteByCondition(LocalDateTime beforeDate, String category) {
    if (!isUserAction()) {
      return;
    }
    // 根据条件删除
    String tenantId = PrincipalContext.get().getTenantId().toString();
    if (beforeDate != null) {
      List<ActivityLog> logs = activityLogRepo.findByTenantIdAndTimestampBefore(
          tenantId, beforeDate);
      if (!isEmpty(logs)) {
        activityLogRepo.deleteAll(logs);
      }
    }
    if (category != null) {
      ActivityCategory categoryEnum = ActivityCategory.valueOf(category);
      List<ActivityLog> logs = activityLogRepo.findByTenantIdAndCategory(
          tenantId, categoryEnum);
      if (!isEmpty(logs)) {
        activityLogRepo.deleteAll(logs);
      }
    }
  }

  @Override
  protected BaseRepository<ActivityLog, String> getRepository() {
    return this.activityLogRepo;
  }
}
