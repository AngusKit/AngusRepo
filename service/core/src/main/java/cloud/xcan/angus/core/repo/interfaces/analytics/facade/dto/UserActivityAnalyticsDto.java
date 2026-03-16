package cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "用户活跃度分析查询参数")
public class UserActivityAnalyticsDto implements Serializable {

  @Schema(description = "统计周期(天)")
  private Integer period = 30;

  @Schema(description = "开始日期")
  private LocalDate startDate;

  @Schema(description = "结束日期")
  private LocalDate endDate;
}
