package cloud.xcan.angus.core.repo.application.cmd.activitylog.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.activitylog.ActivityLogCmd;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityCategory;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLog;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLogRepo;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;

/**
 * 活动日志命令实现
 */
@Biz
public class ActivityLogCmdImpl extends CommCmd<ActivityLog, String> implements ActivityLogCmd {

  @Resource
  private ActivityLogRepo activityLogRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ActivityLog create(ActivityLog activityLog) {
    return new BizTemplate<ActivityLog>() {
      @Override
      protected ActivityLog process() {
        // Null input returns null by design: this method may be called by internal batch
        // processing where null entries should be silently skipped rather than throwing exceptions
        if (Objects.isNull(activityLog)) {
          return null;
        }
        insert0(activityLog);
        return activityLog;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        if (!isUserAction() || Objects.isNull(id)) {
          return null;
        }
        activityLogRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteBatch(List<String> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        if (!isUserAction() || isEmpty(ids)) {
          return null;
        }
        activityLogRepo.deleteAllById(ids);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteByCondition(LocalDateTime beforeDate, String category) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        if (!isUserAction()) {
          return null;
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
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<ActivityLog, String> getRepository() {
    return this.activityLogRepo;
  }
}
