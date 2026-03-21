package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "API请求日志统计查询DTO")
public class InterfaceRequestLogStatisticsDto {

  @Schema(description = "开始时间 yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startDate;

  @Schema(description = "结束时间 yyyy-MM-dd HH:mm:ss")
  private LocalDateTime endDate;

  @Schema(description = "应用编码")
  private String applicationCode;
}
