package cloud.xcan.angus.core.gm.interfaces.log.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;

@Data
@Schema(description = "系统日志统计查询DTO")
public class SystemLogStatisticsDto {

  @Schema(description = "开始日期 yyyy-MM-dd")
  private LocalDate startDate;

  @Schema(description = "结束日期 yyyy-MM-dd")
  private LocalDate endDate;

  @Schema(description = "应用ID")
  private Long applicationId;
}
