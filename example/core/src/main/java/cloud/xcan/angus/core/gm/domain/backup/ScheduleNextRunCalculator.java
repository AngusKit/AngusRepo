package cloud.xcan.angus.core.gm.domain.backup;

import cloud.xcan.angus.core.gm.domain.backup.enums.ScheduleFrequency;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

/**
 * 备份计划下次执行时间计算器
 */
@Slf4j
public final class ScheduleNextRunCalculator {

  private ScheduleNextRunCalculator() {
  }

  /**
   * 根据备份计划计算下次执行时间
   *
   * @param schedule 备份计划
   * @param baseTime 基准时间（通常为当前时间）
   * @return 下次执行时间
   */
  public static LocalDateTime calculate(BackupSchedule schedule, LocalDateTime baseTime) {
    if (schedule.getTime() == null || schedule.getTime().trim().isEmpty()) {
      return baseTime.plusDays(1);
    }

    LocalTime scheduleTime;
    try {
      scheduleTime = LocalTime.parse(schedule.getTime(), DateTimeFormatter.ofPattern("HH:mm"));
    } catch (Exception e) {
      log.warn("备份计划「{}」的时间格式错误：{}，使用默认时间", schedule.getName(),
          schedule.getTime());
      return baseTime.plusDays(1);
    }

    ScheduleFrequency frequency = schedule.getFrequency();
    if (frequency == null) {
      return baseTime.plusDays(1);
    }

    LocalDateTime nextRunTime;
    switch (frequency) {
      case DAILY:
        nextRunTime = calculateDaily(baseTime, scheduleTime);
        break;
      case WEEKLY:
        nextRunTime = calculateWeekly(baseTime, scheduleTime);
        break;
      case MONTHLY:
        nextRunTime = calculateMonthly(baseTime, scheduleTime);
        break;
      default:
        nextRunTime = baseTime.plusDays(1);
    }

    return nextRunTime;
  }

  /**
   * 每日：如果今天的执行时间还没过则用今天，否则用明天
   */
  private static LocalDateTime calculateDaily(LocalDateTime baseTime, LocalTime scheduleTime) {
    LocalDateTime nextRunTime = baseTime.toLocalDate().plusDays(1).atTime(scheduleTime);
    LocalDateTime todayTime = baseTime.toLocalDate().atTime(scheduleTime);
    if (todayTime.isAfter(baseTime)) {
      nextRunTime = todayTime;
    }
    return nextRunTime;
  }

  /**
   * 每周：如果本周同日的执行时间还没过则用本周，否则用下周
   */
  private static LocalDateTime calculateWeekly(LocalDateTime baseTime, LocalTime scheduleTime) {
    LocalDateTime thisWeekTime = baseTime.toLocalDate().atTime(scheduleTime);
    if (thisWeekTime.isAfter(baseTime)) {
      return thisWeekTime;
    }
    return baseTime.toLocalDate().plusWeeks(1).atTime(scheduleTime);
  }

  /**
   * 每月：如果本月同日的执行时间还没过则用本月，否则用下月
   */
  private static LocalDateTime calculateMonthly(LocalDateTime baseTime, LocalTime scheduleTime) {
    LocalDateTime thisMonthTime = baseTime.toLocalDate().atTime(scheduleTime);
    if (thisMonthTime.isAfter(baseTime)) {
      return thisMonthTime;
    }
    return baseTime.toLocalDate().plusMonths(1).atTime(scheduleTime);
  }
}
