package cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "用户活跃度分析结果")
public class UserActivityAnalyticsVo {

  @Schema(description = "活跃用户数")
  private Long activeUsers;

  @Schema(description = "总操作次数")
  private Long totalActions;

  @Schema(description = "日均活跃用户数")
  private Long averageDailyActiveUsers;

  @Schema(description = "活跃度趋势")
  private List<TrendDataPointVo> trendData;
}
