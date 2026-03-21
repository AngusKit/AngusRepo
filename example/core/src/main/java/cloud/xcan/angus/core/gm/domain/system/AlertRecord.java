package cloud.xcan.angus.core.gm.domain.system;

import cloud.xcan.angus.api.commonlink.setting.alert.AlertLevel;
import cloud.xcan.angus.core.gm.domain.system.enums.AlertRecordStatus;
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
import lombok.experimental.Accessors;

/**
 * 告警记录领域实体
 */
@Entity
@Table(name = "gm_system_alert_record")
@Setter
@Getter
@Accessors(chain = true)
public class AlertRecord extends AuditingEntity<AlertRecord, Long> {

  @Id
  private Long id;

  /**
   * 告警规则名称
   */
  @Column(name = "rule_name", nullable = false, length = 200)
  private String ruleName;

  /**
   * 监控指标（cpu_usage, memory_usage, disk_usage等）
   */
  @Column(name = "metric", nullable = false, length = 50)
  private String metric;

  /**
   * 指标显示名称
   */
  @Column(name = "metric_name", length = 100)
  private String metricName;

  /**
   * 当前值
   */
  @Column(name = "current_value", nullable = false)
  private Double currentValue;

  /**
   * 阈值
   */
  @Column(name = "threshold", nullable = false)
  private Double threshold;

  /**
   * 条件（>, >=, <, <=, ==） 注意：condition 是 MySQL 保留关键字，需要使用反引号包裹
   */
  @Column(name = "`condition`", nullable = false, length = 10)
  private String condition;

  /**
   * 告警等级
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "level", nullable = false, length = 20)
  private AlertLevel level;

  /**
   * 状态（ACTIVE, RESOLVED）
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private AlertRecordStatus status = AlertRecordStatus.ACTIVE;

  /**
   * 触发时间
   */
  @Column(name = "trigger_time", nullable = false)
  private LocalDateTime triggerTime;

  /**
   * 恢复时间
   */
  @Column(name = "resolved_time")
  private LocalDateTime resolvedTime;

  /**
   * 描述信息
   */
  @Column(name = "description", length = 1000)
  private String description;

  /**
   * 组件名称（如果是健康检查告警）
   */
  @Column(name = "component_name", length = 100)
  private String componentName;

  /**
   * 组件状态（如果是健康检查告警）
   */
  @Column(name = "component_status", length = 20)
  private String componentStatus;

  /**
   * 实例ID（从Spring ApplicationInfo获取）
   */
  @Column(name = "instance_id", length = 80)
  private String instanceId;

  @Override
  public Long identity() {
    return this.id;
  }
}
