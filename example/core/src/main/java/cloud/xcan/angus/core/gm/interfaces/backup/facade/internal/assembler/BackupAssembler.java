package cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler;

import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.calculateDurationWithSecond;
import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.formatFileSize;

import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.RestoreTask;
import cloud.xcan.angus.core.gm.domain.backup.enums.BackupStatus;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupFindDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupListVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.RestoreHistoryVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BackupAssembler {

  public static Backup toCreateDomain(BackupCreateDto dto) {
    Backup backup = new Backup();
    backup.setName(dto.getName());
    backup.setType(dto.getType());
    backup.setApplicationId(dto.getApplicationId());
    backup.setDescription(dto.getDescription());
    backup.setBackupLogs(Boolean.TRUE.equals(dto.getBackupLogs()));
    backup.setStatus(BackupStatus.PENDING);
    backup.setAutoDelete(true);
    backup.setVerified(false);
    backup.setBackupPath(null);
    return backup;
  }

  public static BackupDetailVo toDetailVo(Backup backup) {
    BackupDetailVo vo = new BackupDetailVo();
    vo.setId(backup.getId());
    vo.setName(backup.getName());
    vo.setType(backup.getType());
    vo.setApplicationId(backup.getApplicationId());
    vo.setStatus(backup.getStatus());
    vo.setSourcePath(backup.getSourcePath());
    vo.setBackupPath(backup.getBackupPath());
    vo.setFileSize(backup.getFileSize());
    vo.setSize(formatFileSize(backup.getFileSize() != null ? backup.getFileSize() : 0));
    vo.setPath(backup.getBackupPath());
    vo.setStartTime(backup.getStartTime());
    vo.setEndTime(backup.getEndTime());
    vo.setDuration(calculateDurationWithSecond(backup.getStartTime(), backup.getEndTime()));
    vo.setRetentionDays(backup.getRetentionDays());
    vo.setAutoDelete(backup.getAutoDelete());
    vo.setVerified(backup.getVerified());
    vo.setDescription(backup.getDescription());
    vo.setCanRestore(backup.getStatus() == BackupStatus.COMPLETED);

    // 设置审计字段
    vo.setCreatedBy(backup.getCreatedBy());
    vo.setCreatedDate(backup.getCreatedDate());
    vo.setModifiedBy(backup.getModifiedBy());
    vo.setModifiedDate(backup.getModifiedDate());
    return vo;
  }

  public static List<RestoreHistoryVo> toHistoryVo(List<RestoreTask> historyList) {
    return historyList.stream()
        .map(BackupAssembler::toRestoreHistoryVo)
        .collect(Collectors.toList());
  }

  public static RestoreHistoryVo toRestoreHistoryVo(RestoreTask history) {
    RestoreHistoryVo vo = new RestoreHistoryVo();
    vo.setRestoreTime(history.getEndTime());
    vo.setRestoreBy(history.getCreatedBy());
    vo.setRestoreStatus(history.getStatus());
    return vo;
  }

  public static BackupListVo toListVo(Backup backup) {
    BackupListVo vo = new BackupListVo();
    vo.setId(backup.getId());
    vo.setName(backup.getName());
    vo.setType(backup.getType());
    vo.setStatus(backup.getStatus());
    vo.setErrorMessage(backup.getErrorMessage());
    vo.setFileSize(backup.getFileSize());
    vo.setSize(formatFileSize(backup.getFileSize() != null ? backup.getFileSize() : 0));
    vo.setPath(backup.getBackupPath());
    vo.setDuration(calculateDurationWithSecond(backup.getStartTime(), backup.getEndTime()));
    vo.setStartTime(backup.getStartTime());
    vo.setEndTime(backup.getEndTime());

    // 设置审计字段
    vo.setCreatedBy(backup.getCreatedBy());
    vo.setCreatedDate(backup.getCreatedDate());
    vo.setModifiedBy(backup.getModifiedBy());
    vo.setModifiedDate(backup.getModifiedDate());
    return vo;
  }

  public static GenericSpecification<Backup> getSpecification(BackupFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .orderByFields("id", "createdDate", "name")
        .matchSearchFields("name", "description")
        .build();
    return new GenericSpecification<>(filters);
  }
}
