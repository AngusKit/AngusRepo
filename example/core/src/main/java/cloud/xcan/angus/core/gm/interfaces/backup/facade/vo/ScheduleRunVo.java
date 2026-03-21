package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "备份计划执行响应")
public class ScheduleRunVo {

  @Schema(description = "计划ID")
  private Long scheduleId;

  @Schema(description = "备份ID")
  private Long backupId;

  @Schema(description = "开始时间")
  private LocalDateTime startTime;
}
