package cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler;

import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.calculateDurationWithSecond;

import cloud.xcan.angus.core.gm.domain.backup.RestoreOptions;
import cloud.xcan.angus.core.gm.domain.backup.RestoreTask;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupValidateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.RestoreCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.RestoreFindDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.RestoreOptionsDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupValidationVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.RestoreTaskDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.RestoreTaskListVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class RestoreTaskAssembler {

  public static RestoreTask toCreateDomain(RestoreCreateDto dto) {
    RestoreTask task = new RestoreTask();
    task.setSource(dto.getSource());
    task.setBackupId(dto.getBackupId());
    task.setFilePath(dto.getFilePath());
    task.setOptions(toRestoreOptions(dto.getOptions()));
    task.setPassword(dto.getPassword());
    return task;
  }

  public static RestoreTaskDetailVo toDetailVo(RestoreTask task) {
    RestoreTaskDetailVo vo = new RestoreTaskDetailVo();
    vo.setId(task.getId());
    vo.setSource(task.getSource());
    vo.setBackupId(task.getBackupId());
    vo.setBackupName(task.getBackupName());
    vo.setFilePath(task.getFilePath());
    vo.setOptions(task.getOptions());
    vo.setStatus(task.getStatus());
    vo.setStartTime(task.getStartTime());
    vo.setEndTime(task.getEndTime());
    vo.setDuration(calculateDurationWithSecond(task.getStartTime(), task.getEndTime()));
    vo.setProgress(task.getProgress());
    vo.setErrorMessage(task.getErrorMessage());

    // 设置审计字段
    vo.setCreatedBy(task.getCreatedBy());
    vo.setCreatedDate(task.getCreatedDate());
    vo.setModifiedBy(task.getModifiedBy());
    vo.setModifiedDate(task.getModifiedDate());
    return vo;
  }

  public static RestoreTaskListVo toListVo(RestoreTask task) {
    RestoreTaskListVo vo = new RestoreTaskListVo();
    vo.setId(task.getId());
    vo.setSource(task.getSource());
    vo.setBackupId(task.getBackupId());
    vo.setBackupName(task.getBackupName());
    vo.setFilePath(task.getFilePath());
    vo.setStatus(task.getStatus());
    vo.setStartTime(task.getStartTime());
    vo.setEndTime(task.getEndTime());
    vo.setDuration(calculateDurationWithSecond(task.getStartTime(), task.getEndTime()));
    vo.setProgress(task.getProgress());

    // 设置审计字段
    vo.setCreatedBy(task.getCreatedBy());
    vo.setCreatedDate(task.getCreatedDate());
    vo.setModifiedBy(task.getModifiedBy());
    vo.setModifiedDate(task.getModifiedDate());
    return vo;
  }

  private static RestoreOptions toRestoreOptions(RestoreOptionsDto dto) {
    RestoreOptions options = new RestoreOptions();
    options.setRestoreDatabase(dto.getRestoreDatabase());
    options.setRestoreConfig(dto.getRestoreConfig());
    options.setRestoreFiles(dto.getRestoreFiles());
    options.setRestoreLogs(dto.getRestoreLogs());
    return options;
  }

  public static BackupValidationVo toValidationVo(BackupValidateDto dto) {
    // TODO: 实现备份文件验证逻辑
    BackupValidationVo vo = new BackupValidationVo();
    vo.setValid(true);
    vo.setCompatible(true);
    vo.setMessages(java.util.Collections.emptyList());
    return vo;
  }

  public static GenericSpecification<RestoreTask> getSpecification(RestoreFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "startTime")
        .orderByFields("id", "createdDate", "startTime", "status")
        .build();
    return new GenericSpecification<>(filters);
  }

}
