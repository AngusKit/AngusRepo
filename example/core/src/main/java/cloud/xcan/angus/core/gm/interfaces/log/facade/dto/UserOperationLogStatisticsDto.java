package cloud.xcan.angus.core.gm.interfaces.log.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "用户操作日志统计查询DTO")
public class UserOperationLogStatisticsDto {

  @Schema(description = "开始时间 yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startDate;

  @Schema(description = "结束时间 yyyy-MM-dd HH:mm:ss")
  private LocalDateTime endDate;

}
