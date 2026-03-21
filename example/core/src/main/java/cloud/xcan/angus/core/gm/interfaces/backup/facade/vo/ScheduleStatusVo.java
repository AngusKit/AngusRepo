package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "备份计划状态信息")
public class ScheduleStatusVo {

  @Schema(description = "计划ID")
  private Long id;

  @Schema(description = "计划状态")
  private EnabledStatus status;

  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;
}
