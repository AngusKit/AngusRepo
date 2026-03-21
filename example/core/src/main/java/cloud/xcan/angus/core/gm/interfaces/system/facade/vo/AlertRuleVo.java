package cloud.xcan.angus.core.gm.interfaces.system.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "告警规则VO")
public class AlertRuleVo {

  @Schema(description = "规则ID", example = "1")
  private Long id;

  @Schema(description = "规则名称", example = "内存使用率告警")
  private String name;

  @Schema(description = "监控指标", example = "memory_usage")
  private String metric;

  @Schema(description = "条件", example = ">")
  private String condition;

  @Schema(description = "阈值", example = "90.0")
  private Double threshold;

  @Schema(description = "持续时间（秒）", example = "300")
  private Integer duration;

  @Schema(description = "告警等级", example = "HIGH")
  private String level;

  @Schema(description = "触发次数", example = "5")
  private Long triggerCount;
}
