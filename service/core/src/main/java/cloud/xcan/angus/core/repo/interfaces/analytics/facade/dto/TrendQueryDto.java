package cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "趋势查询参数")
public class TrendQueryDto {

  @Schema(description = "统计周期(7/30/90天)")
  private Integer period = 30;

  @Schema(description = "开始日期")
  private LocalDate startDate;

  @Schema(description = "结束日期")
  private LocalDate endDate;

  @Schema(description = "仓库ID筛选")
  private Long repositoryId;
}
