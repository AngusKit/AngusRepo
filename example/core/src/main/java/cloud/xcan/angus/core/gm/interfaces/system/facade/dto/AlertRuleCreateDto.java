package cloud.xcan.angus.core.gm.interfaces.system.facade.dto;

import cloud.xcan.angus.api.commonlink.setting.alert.AlertLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "创建告警规则DTO")
public class AlertRuleCreateDto {

  @NotBlank
  @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "内存使用率告警")
  private String name;

  @NotBlank
  @Schema(description = "监控指标", requiredMode = Schema.RequiredMode.REQUIRED, example = "memory_usage")
  private String metric;

  @NotBlank
  @Schema(description = "条件", requiredMode = Schema.RequiredMode.REQUIRED, example = ">")
  private String condition;

  @NotNull
  @Schema(description = "阈值", requiredMode = Schema.RequiredMode.REQUIRED, example = "90")
  private Double threshold;

  @Schema(description = "持续时间（秒）", example = "300")
  private Integer duration;

  @NotNull
  @Schema(description = "告警等级", requiredMode = Schema.RequiredMode.REQUIRED, example = "HIGH")
  private AlertLevel level;
}
