package cloud.xcan.angus.core.gm.interfaces.system.facade.vo;

import cloud.xcan.angus.api.commonlink.setting.alert.AlertLevel;
import cloud.xcan.angus.core.gm.domain.system.enums.AlertRecordStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "告警记录VO")
public class AlertRecordVo {

  @Schema(description = "告警记录ID", example = "1")
  private Long id;

  @Schema(description = "规则ID", example = "1")
  private Long ruleId;

  @Schema(description = "规则名称", example = "内存使用率告警")
  private String ruleName;

  @Schema(description = "监控指标", example = "memory_usage")
  private String metric;

  @Schema(description = "当前值", example = "95.5")
  private Double currentValue;

  @Schema(description = "阈值", example = "90.0")
  private Double threshold;

  @Schema(description = "告警消息", example = "内存使用率当前值为 95.50%，超过阈值 90.00%（条件：>）")
  private String message;

  @Schema(description = "告警等级", example = "HIGH")
  private AlertLevel level;

  @Schema(description = "告警状态", example = "ACTIVE")
  private AlertRecordStatus status;

  @Schema(description = "触发时间", example = "2025-12-19T10:30:00")
  private LocalDateTime triggerTime;

  @Schema(description = "实例ID", example = "192.168.1.100:1806")
  private String instanceId;
}
