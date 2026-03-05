package cloud.xcan.angus.core.repo.interfaces.system.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "系统重启请求参数")
public class SystemRestartDto {

  @Schema(description = "是否进入维护模式")
  private Boolean maintenanceMode;

  @Schema(description = "重启原因")
  private String reason;

  @Schema(description = "预计维护时间（分钟）")
  private Integer estimatedMinutes;
}
