package cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.BackupSchedule;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.ScheduleCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.ScheduleUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.ScheduleDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.ScheduleRunVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.ScheduleStatusVo;

public class ScheduleAssembler {

  public static BackupSchedule toCreateDomain(ScheduleCreateDto dto) {
    BackupSchedule schedule = new BackupSchedule();
    schedule.setName(dto.getName());
    schedule.setType(dto.getType());
    schedule.setFrequency(dto.getFrequency());
    schedule.setApplicationId(dto.getApplicationId());
    schedule.setTime(dto.getTime());
    schedule.setRetention(dto.getRetention());
    schedule.setStatus(nullSafe(dto.getStatus(), EnabledStatus.ENABLED));
    schedule.setBackupLogs(Boolean.TRUE.equals(dto.getBackupLogs()));
    return schedule;
  }

  public static BackupSchedule toUpdateDomain(Long id, ScheduleUpdateDto dto) {
    BackupSchedule schedule = new BackupSchedule();
    schedule.setId(id);
    schedule.setName(dto.getName());
    schedule.setType(dto.getType());
    schedule.setFrequency(dto.getFrequency());
    schedule.setApplicationId(dto.getApplicationId());
    schedule.setTime(dto.getTime());
    schedule.setRetention(dto.getRetention());
    if (dto.getBackupLogs() != null) {
      schedule.setBackupLogs(dto.getBackupLogs());
    }
    return schedule;
  }

  public static ScheduleStatusVo toScheduleStatusVo(Long id, BackupSchedule schedule) {
    ScheduleStatusVo vo = new ScheduleStatusVo();
    vo.setId(id);
    vo.setStatus(schedule.getStatus());
    vo.setModifiedDate(schedule.getModifiedDate());
    return vo;
  }

  public static BackupCreateDto toBackupCreateDto(String backupName,
      BackupSchedule schedule, String backupDescription) {
    BackupCreateDto backupDto = new BackupCreateDto();
    backupDto.setName(backupName);
    backupDto.setType(schedule.getType());
    backupDto.setDescription(backupDescription);
    backupDto.setApplicationId(schedule.getApplicationId());
    backupDto.setBackupLogs(Boolean.TRUE.equals(schedule.getBackupLogs()));
    return backupDto;
  }

  public static ScheduleRunVo toScheduleRunVo(Long id, Backup created) {
    ScheduleRunVo vo = new ScheduleRunVo();
    vo.setScheduleId(id);
    vo.setBackupId(created.getId());
    vo.setStartTime(created.getStartTime());
    return vo;
  }

  public static ScheduleDetailVo toDetailVo(BackupSchedule schedule) {
    ScheduleDetailVo vo = new ScheduleDetailVo();
    vo.setId(schedule.getId());
    vo.setName(schedule.getName());
    vo.setType(schedule.getType());
    vo.setFrequency(schedule.getFrequency());
    vo.setApplicationId(schedule.getApplicationId());
    vo.setTime(schedule.getTime());
    vo.setRetention(schedule.getRetention());
    vo.setStatus(schedule.getStatus());
    vo.setLastRun(schedule.getLastRunTime());
    vo.setNextRun(schedule.getNextRunTime());
    vo.setCreatedAt(schedule.getCreatedDate());
    vo.setBackupLogs(Boolean.TRUE.equals(schedule.getBackupLogs()));

    // 设置审计信息
    vo.setCreatedBy(schedule.getCreatedBy());
    vo.setCreatedDate(schedule.getCreatedDate());
    vo.setModifiedBy(schedule.getModifiedBy());
    vo.setModifiedDate(schedule.getModifiedDate());
    return vo;
  }
}
