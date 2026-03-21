package cloud.xcan.angus.core.gm.domain.log;

import cloud.xcan.angus.core.gm.domain.log.enums.LogStatus;
import cloud.xcan.angus.core.gm.domain.log.enums.LogType;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统日志文件实体
 */
@Getter
@Setter
@Entity
@Table(name = "gm_system_log")
public class SystemLog extends EntitySupport<SystemLog, Long> {

  @Id
  private Long id;

  /**
   * 文件名
   */
  @Column(name = "filename", nullable = false, length = 255)
  private String filename;

  /**
   * 文件路径
   */
  @Column(name = "file_path", nullable = false, length = 500)
  private String filePath;

  /**
   * 文件大小（字节）
   */
  @Column(name = "size", nullable = false)
  private Long size;

  /**
   * 行数
   */
  @Column(name = "line_count")
  private Long lineCount;

  /**
   * 日志类型
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private LogType type;

  /**
   * 日志日期
   */
  @Column(name = "date", nullable = false)
  private LocalDate date;

  /**
   * 应用ID
   */
  @Column(name = "application_id", nullable = false)
  private Long applicationId;

  @Column(name = "service_code", length = 100)
  private String serviceCode;

  @Column(name = "service_name", length = 100)
  private String serviceName;

  /**
   * 为Eureka实例ID，格式：IP:PORT
   */
  @Column(name = "instance_id", length = 80)
  private String instanceId;

  /**
   * 状态
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private LogStatus status;

  /**
   * 是否压缩
   */
  @Column(name = "compressed", nullable = false)
  private Boolean compressed = false;

  /**
   * 文件编码
   */
  @Column(name = "encoding", length = 20)
  private String encoding = "UTF-8";

  /**
   * 创建记录时间
   */
  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  protected LocalDateTime createdDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
