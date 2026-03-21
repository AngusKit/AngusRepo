package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "服务统计")
public class ServiceStatsVo {

  @Schema(description = "总服务数")
  private Long totalServices;

  @Schema(description = "总实例数")
  private Long totalInstances;

  @Schema(description = "运行中实例数")
  private Long upInstances;

  @Schema(description = "停止实例数")
  private Long downInstances;
}
