package cloud.xcan.angus.core.gm.interfaces.log.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "更新日志清理配置DTO")
public class LogRetentionConfigUpdateDto {

  @NotNull
  @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long applicationId;

  @NotNull
  @Min(1)
  @Max(365)
  @Schema(description = "用户日志保留天数 (1-365)，默认90天", requiredMode = Schema.RequiredMode.REQUIRED)
  private int userLogRetentionDays = 90;

  @NotNull
  @Min(1)
  @Max(365)
  @Schema(description = "系统日志保留天数 (1-365)，默认60天", requiredMode = Schema.RequiredMode.REQUIRED)
  private int systemLogRetentionDays = 60;

  @NotNull
  @Min(1)
  @Max(365)
  @Schema(description = "API日志保留天数 (1-365)，默认30天", requiredMode = Schema.RequiredMode.REQUIRED)
  private int apiLogRetentionDays = 30;

}
