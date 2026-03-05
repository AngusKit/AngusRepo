package cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "趋势数据点")
public class TrendDataPointVo {

  @Schema(description = "日期")
  private LocalDate date;

  @Schema(description = "值")
  private Long value;
}
