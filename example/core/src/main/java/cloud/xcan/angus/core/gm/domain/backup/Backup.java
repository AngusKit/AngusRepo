package cloud.xcan.angus.core.gm.domain.backup;

import cloud.xcan.angus.core.gm.domain.backup.enums.BackupStatus;
import cloud.xcan.angus.core.gm.domain.backup.enums.BackupType;
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
@Table(name = "gm_backup")
public class Backup extends AuditingEntity<Backup, Long> {

  @Id
  private Long id;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 200, nullable = false)
  private BackupType type;

  /**
   * 备份应用ID，不指定时备份所有应用
   */
  @Column(name = "application_id")
  private Long applicationId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20, nullable = false)
  private BackupStatus status;

  @Column(name = "source_path", length = 400)
  private String sourcePath;

  @Column(name = "backup_path", length = 500, nullable = false)
  private String backupPath;

  @Column(name = "file_size")
  private Long fileSize;

  @Column(name = "start_time")
  private LocalDateTime startTime;

  @Column(name = "end_time")
  private LocalDateTime endTime;

  @Column(name = "retention_days")
  private Integer retentionDays;

  @Column(name = "auto_delete", nullable = false)
  private Boolean autoDelete = true;

  @Column(name = "verified", nullable = false)
  private Boolean verified = false;

  @Column(name = "error_message", length = 1000)
  private String errorMessage;

  @Column(name = "description", length = 500)
  private String description;

  @Column(name = "backup_logs")
  private Boolean backupLogs;

  @Override
  public Long identity() {
    return id;
  }
}
