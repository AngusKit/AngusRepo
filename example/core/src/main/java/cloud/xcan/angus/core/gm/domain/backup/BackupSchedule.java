package cloud.xcan.angus.core.gm.domain.backup;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.backup.enums.BackupType;
import cloud.xcan.angus.core.gm.domain.backup.enums.ScheduleFrequency;
import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "gm_backup_schedule")
public class BackupSchedule extends AuditingEntity<BackupSchedule, Long> {

  @Id
  private Long id;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 20, nullable = false)
  private BackupType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "frequency", length = 20, nullable = false)
  private ScheduleFrequency frequency;

  /**
   * 备份应用ID，不指定时备份所有应用
   */
  @Column(name = "application_id")
  private Long applicationId;

  @Column(name = "time", length = 20)
  private String time;

  @Column(name = "retention", length = 50)
  private String retention;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20, nullable = false)
  private EnabledStatus status;

  @Column(name = "last_run_time")
  private LocalDateTime lastRunTime;

  @Column(name = "next_run_time")
  private LocalDateTime nextRunTime;

  @Column(name = "description", length = 500)
  private String description;

  @Column(name = "backup_logs")
  private Boolean backupLogs;

  @Override
  public Long identity() {
    return id;
  }
}
