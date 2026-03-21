package cloud.xcan.angus.core.gm.application.cmd.backup.impl;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.PermissionCheck;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.backup.BackupScheduleCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.query.backup.BackupScheduleQuery;
import cloud.xcan.angus.core.gm.domain.backup.BackupSchedule;
import cloud.xcan.angus.core.gm.domain.backup.ScheduleNextRunCalculator;
import cloud.xcan.angus.core.gm.domain.backup.BackupScheduleRepo;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.remote.message.ProtocolException;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BackupScheduleCmdImpl extends CommCmd<BackupSchedule, Long> implements
    BackupScheduleCmd {

  @Resource
  private BackupScheduleRepo scheduleRepo;

  @Resource
  private BackupScheduleQuery scheduleQuery;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public BackupSchedule create(BackupSchedule schedule) {
    return new BizTemplate<BackupSchedule>() {
      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();

        if (scheduleQuery.existsByName(schedule.getName())) {
          throw ProtocolException.of("备份计划名称「{0}」已存在", new Object[]{schedule.getName()});
        }

        // 限制最多创建10个备份计划
        List<BackupSchedule> allSchedules = scheduleQuery.findAll();
        if (allSchedules.size() >= 10) {
          throw ProtocolException.of("备份计划数量已达上限，最多只能创建10个备份计划");
        }

        // 时间间隔不能小于30分钟
        if (schedule.getTime() != null && !schedule.getTime().trim().isEmpty()) {
          LocalTime newScheduleTime = parseTime(schedule.getTime());
          for (BackupSchedule existingSchedule : allSchedules) {
            if (existingSchedule.getTime() != null
                && !existingSchedule.getTime().trim().isEmpty()) {
              LocalTime existingTime = parseTime(existingSchedule.getTime());
              long minutesDiff = Math.abs(
                  Duration.between(existingTime, newScheduleTime).toMinutes());
              // 处理跨天的情况（如23:30和00:00）
              if (minutesDiff > 12 * 60) {
                minutesDiff = 24 * 60 - minutesDiff;
              }
              if (minutesDiff < 30) {
                throw ProtocolException.of(
                    "备份计划时间间隔不能小于30分钟，当前时间「{0}」与计划「{1}」的时间「{2}」间隔仅{3}分钟",
                    new Object[]{schedule.getTime(), existingSchedule.getName(),
                        existingSchedule.getTime(), minutesDiff});
              }
            }
          }
        }
      }

      @Override
      protected BackupSchedule process() {
        insert(schedule);

        // 记录操作日志
        String scheduleName = schedule.getName();
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.CONFIG,
            schedule.getId(),
            scheduleName,
            OperationMessage.BACKUP_SCHEDULE_CREATE_DETAILS,
            new Object[]{scheduleName}
        );

        return schedule;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public BackupSchedule update(BackupSchedule schedule) {
    return new BizTemplate<BackupSchedule>() {
      BackupSchedule existing;

      @Override
      protected void checkParams() {
        existing = scheduleQuery.findAndCheck(schedule.getId());
        if (!existing.getName().equals(schedule.getName())
            && scheduleQuery.existsByName(schedule.getName())) {
          throw ProtocolException.of("备份计划名称「{0}」已存在", new Object[]{schedule.getName()});
        }

        // 时间间隔不能小于30分钟（更新时也需要检查）
        if (schedule.getTime() != null && !schedule.getTime().trim().isEmpty()) {
          LocalTime newScheduleTime = parseTime(schedule.getTime());
          List<BackupSchedule> allSchedules = scheduleQuery.findAll();
          for (BackupSchedule existingSchedule : allSchedules) {
            // 排除当前正在更新的计划
            if (existingSchedule.getId().equals(existing.getId())) {
              continue;
            }
            if (existingSchedule.getTime() != null
                && !existingSchedule.getTime().trim().isEmpty()) {
              LocalTime existingTime = parseTime(existingSchedule.getTime());
              long minutesDiff = Math.abs(
                  java.time.Duration.between(existingTime, newScheduleTime).toMinutes());
              // 处理跨天的情况（如23:30和00:00）
              if (minutesDiff > 12 * 60) {
                minutesDiff = 24 * 60 - minutesDiff;
              }
              if (minutesDiff < 30) {
                throw ProtocolException.of(
                    "备份计划时间间隔不能小于30分钟，当前时间「{0}」与计划「{1}」的时间「{2}」间隔仅{3}分钟",
                    new Object[]{schedule.getTime(), existingSchedule.getName(),
                        existingSchedule.getTime(), minutesDiff});
              }
            }
          }
        }
      }

      @Override
      protected BackupSchedule process() {
        CoreUtils.copyPropertiesIgnoreNull(schedule, existing);
        existing.setNextRunTime(ScheduleNextRunCalculator.calculate(existing, LocalDateTime.now()));
        scheduleRepo.save(existing);

        // 记录操作日志
        String scheduleName = existing.getName();
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            existing.getId(),
            scheduleName,
            OperationMessage.BACKUP_SCHEDULE_UPDATE_DETAILS,
            new Object[]{scheduleName}
        );

        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateStatus(Long id, EnabledStatus status) {
    new BizTemplate<Void>() {
      BackupSchedule schedule;

      @Override
      protected void checkParams() {
        schedule = scheduleQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        schedule.setStatus(status);
        scheduleRepo.save(schedule);

        // 记录操作日志
        String scheduleName = schedule.getName();
        if (EnabledStatus.ENABLED.equals(status)) {
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.CONFIG,
              id,
              scheduleName,
              OperationMessage.BACKUP_SCHEDULE_ENABLE_DETAILS,
              new Object[]{scheduleName}
          );
        } else if (EnabledStatus.DISABLED.equals(status)) {
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.CONFIG,
              id,
              scheduleName,
              OperationMessage.BACKUP_SCHEDULE_DISABLE_DETAILS,
              new Object[]{scheduleName}
          );
        }

        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      BackupSchedule schedule;

      @Override
      protected void checkParams() {
        schedule = scheduleQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        // 保存计划名称用于操作日志（删除前获取）
        String scheduleName = schedule.getName();

        scheduleRepo.deleteById(id);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.CONFIG,
            id,
            scheduleName,
            OperationMessage.BACKUP_SCHEDULE_DELETE_DETAILS,
            new Object[]{scheduleName}
        );

        return null;
      }
    }.execute();
  }

  /**
   * 解析时间字符串为LocalTime
   */
  private LocalTime parseTime(String timeStr) {
    try {
      return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
    } catch (DateTimeParseException e) {
      throw ProtocolException.of("时间格式错误「{0}」，应为HH:mm格式", new Object[]{timeStr});
    }
  }

  @Override
  protected BaseRepository<BackupSchedule, Long> getRepository() {
    return scheduleRepo;
  }
}
