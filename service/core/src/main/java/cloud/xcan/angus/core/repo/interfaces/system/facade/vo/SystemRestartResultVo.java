package cloud.xcan.angus.core.repo.interfaces.system.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "系统重启结果")
public class SystemRestartResultVo {

  @Schema(description = "是否成功")
  private Boolean success;

  @Schema(description = "消息")
  private String message;

  @Schema(description = "预计恢复时间（分钟）")
  private Integer estimatedMinutes;
}
