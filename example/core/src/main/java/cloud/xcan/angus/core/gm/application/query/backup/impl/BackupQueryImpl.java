package cloud.xcan.angus.core.gm.application.query.backup.impl;

import static cloud.xcan.angus.api.commonlink.setting.Setting.getDefaultBackupSettings;
import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.formatFileSize;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.backup.BackupSettings;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.backup.BackupQuery;
import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.BackupRepo;
import cloud.xcan.angus.core.gm.domain.backup.BackupSearchRepo;
import cloud.xcan.angus.core.gm.domain.backup.enums.BackupStatus;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class BackupQueryImpl implements BackupQuery {

  @Resource
  private BackupRepo backupRepo;

  @Resource
  private BackupSearchRepo backupSearchRepo;

  @Resource
  private SettingManager settingManager;

  @Override
  public Backup findAndCheck(Long id) {
    return new BizTemplate<Backup>() {
      @Override
      protected Backup process() {
        return backupRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("备份「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Page<Backup> find(GenericSpecification<Backup> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Backup>>() {
      @Override
      protected Page<Backup> process() {
        return fullTextSearch
            ? backupSearchRepo.find(spec.getCriteria(), pageable, Backup.class, match)
            : backupRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public BackupStatsVo getStats() {
    return new BizTemplate<BackupStatsVo>() {
      @Override
      protected BackupStatsVo process() {
        List<Backup> all = backupRepo.findAll();
        BackupStatsVo stats = new BackupStatsVo();
        stats.setTotalBackups((long) all.size());
        stats.setSuccessBackups(
            all.stream().filter(b -> b.getStatus() == BackupStatus.COMPLETED).count());
        stats.setFailedBackups(
            all.stream().filter(b -> b.getStatus() == BackupStatus.FAILED).count());

        long totalSizeBytes = all.stream()
            .mapToLong(b -> b.getFileSize() != null ? b.getFileSize() : 0)
            .sum();
        stats.setTotalSize(formatFileSize(totalSizeBytes));
        if (!all.isEmpty()) {
          LocalDateTime lastBackupTime = all.stream()
              .map(Backup::getCreatedDate)
              .filter(Objects::nonNull)
              .max(LocalDateTime::compareTo)
              .orElse(null);
          if (lastBackupTime != null) {
            stats.setLastBackupTime(lastBackupTime);
          }
        }
        return stats;
      }
    }.execute();
  }

  @Override
  public List<Backup> findAll() {
    return new BizTemplate<List<Backup>>() {
      @Override
      protected List<Backup> process() {
        return backupRepo.findAll();
      }
    }.execute();
  }

  @Override
  public Long calculateUsedStorageSize() {
    List<Backup> backups = findAll();
    if (backups == null || backups.isEmpty()) {
      return 0L;
    }
    return backups.stream()
        .mapToLong(b -> b.getFileSize() != null ? b.getFileSize() : 0)
        .sum();
  }

  @Override
  public String getStoragePath() {
    BackupSettings backupSettings = getBackupSettings();
    return backupSettings.getStoragePath();
  }

  @Override
  public BackupSettings getBackupSettings() {
    Setting settings = settingManager.getSetting0(SettingKey.BACKUP_SETTINGS);
    return nullSafe(settings != null ? settings.getBackupSettings() : null,
        getDefaultBackupSettings());
  }
}
