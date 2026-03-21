package cloud.xcan.angus.core.gm.interfaces.backup.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler.ScheduleAssembler.toBackupCreateDto;
import static cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler.ScheduleAssembler.toScheduleStatusVo;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.backup.BackupCmd;
import cloud.xcan.angus.core.gm.application.cmd.backup.BackupScheduleCmd;
import cloud.xcan.angus.core.gm.application.query.backup.BackupScheduleQuery;
import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.BackupSchedule;
import cloud.xcan.angus.core.gm.domain.backup.ScheduleNextRunCalculator;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.BackupScheduleFacade;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.ScheduleCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.ScheduleUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler.BackupAssembler;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler.ScheduleAssembler;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.ScheduleDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.ScheduleRunVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.ScheduleStatusVo;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BackupScheduleFacadeImpl implements BackupScheduleFacade {

  @Resource
  private BackupScheduleCmd scheduleCmd;

  @Resource
  private BackupScheduleQuery scheduleQuery;

  @Resource
  private BackupCmd backupCmd;

  @NameJoin
  @Override
  public ScheduleDetailVo createSchedule(ScheduleCreateDto dto) {
    BackupSchedule schedule = ScheduleAssembler.toCreateDomain(dto);
    BackupSchedule created = scheduleCmd.create(schedule);
    return ScheduleAssembler.toDetailVo(created);
  }

  @NameJoin
  @Override
  public ScheduleDetailVo updateSchedule(Long id, ScheduleUpdateDto dto) {
    BackupSchedule updated = ScheduleAssembler.toUpdateDomain(id, dto);
    BackupSchedule saved = scheduleCmd.update(updated);
    return ScheduleAssembler.toDetailVo(saved);
  }

  @Override
  public ScheduleStatusVo updateScheduleStatus(Long id, EnabledStatusUpdateDto dto) {
    scheduleCmd.updateStatus(id, dto.getStatus());
    BackupSchedule schedule = scheduleQuery.findAndCheck(id);
    return toScheduleStatusVo(id, schedule);
  }

  @Override
  public ScheduleRunVo runSchedule(Long id) {
    BackupSchedule schedule = scheduleQuery.findAndCheck(id);
    String backupName = schedule.getName() + "_" + LocalDateTime.now().format(
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    String backupDescription = "由备份计划「" + schedule.getName() + "」自动创建";

    BackupCreateDto backupDto = toBackupCreateDto(backupName, schedule, backupDescription);
    Backup backup = BackupAssembler.toCreateDomain(backupDto);
    Backup created = backupCmd.create(backup);

    schedule.setLastRunTime(LocalDateTime.now());
    schedule.setNextRunTime(ScheduleNextRunCalculator.calculate(schedule, LocalDateTime.now()));
    scheduleCmd.update(schedule);
    return ScheduleAssembler.toScheduleRunVo(id, created);
  }

  @Override
  public void deleteSchedule(Long id) {
    scheduleCmd.delete(id);
  }

  @NameJoin
  @Override
  public List<ScheduleDetailVo> listSchedules() {
    List<BackupSchedule> schedules = scheduleQuery.findAll();
    return schedules.stream()
        .map(ScheduleAssembler::toDetailVo)
        .collect(java.util.stream.Collectors.toList());
  }
}
