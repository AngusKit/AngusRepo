package cloud.xcan.angus.core.gm.domain.backup;

import cloud.xcan.angus.core.gm.domain.backup.enums.RestoreSource;
import cloud.xcan.angus.core.gm.domain.backup.enums.RestoreStatus;
import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "gm_backup_restore_task")
public class RestoreTask extends AuditingEntity<RestoreTask, Long> {

  @Id
  private Long id;

  /**
   * 恢复源类型
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "source", length = 20, nullable = false)
  private RestoreSource source;

  /**
   * 备份ID（source=BACKUP时使用）
   */
  @Column(name = "backup_id")
  private Long backupId;

  /**
   * 文件路径（source=FILE_PATH时使用）
   */
  @Column(name = "file_path", length = 500)
  private String filePath;

  /**
   * 恢复选项
   */
  @Type(JsonType.class)
  @Column(name = "options", columnDefinition = "json", nullable = false)
  private RestoreOptions options;

  /**
   * 恢复状态
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20, nullable = false)
  private RestoreStatus status;

  /**
   * 开始时间
   */
  @Column(name = "start_time")
  private LocalDateTime startTime;

  /**
   * 结束时间
   */
  @Column(name = "end_time")
  private LocalDateTime endTime;

  /**
   * 进度百分比(0-100)
   */
  @Column(name = "progress")
  private Integer progress;

  /**
   * 当前步骤
   */
  @Column(name = "current_step", length = 200)
  private String currentStep;

  /**
   * 总步骤数
   */
  @Column(name = "total_steps")
  private Integer totalSteps;

  /**
   * 已完成步骤数
   */
  @Column(name = "completed_steps")
  private Integer completedSteps;

  /**
   * 预计结束时间
   */
  @Column(name = "estimated_end_time")
  private LocalDateTime estimatedEndTime;

  /**
   * 错误信息
   */
  @Column(name = "error_message", length = 2000)
  private String errorMessage;

  /**
   * 备份名称（非持久化字段，用于临时存储）
   */
  @Transient
  private String backupName;
  /**
   * 恢复备份管理员密码
   */
  @Transient
  private String password;

  @Override
  public Long identity() {
    return id;
  }
}
