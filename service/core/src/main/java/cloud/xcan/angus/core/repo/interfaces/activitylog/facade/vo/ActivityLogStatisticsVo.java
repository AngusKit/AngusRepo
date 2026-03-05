package cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo;

import cloud.xcan.angus.core.repo.domain.activitylog.ActivityAction;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 活动日志统计视图对象
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "活动日志统计信息")
public class ActivityLogStatisticsVo {

  @Schema(description = "总日志数")
  private Long totalLogs;

  @Schema(description = "今日日志数")
  private Long logsToday;

  @Schema(description = "本周日志数")
  private Long logsThisWeek;

  @Schema(description = "本月日志数")
  private Long logsThisMonth;

  @Schema(description = "操作类型分布")
  private Map<ActivityAction, Long> actionDistribution;

  @Schema(description = "分类分布")
  private Map<ActivityCategory, Long> categoryDistribution;

  @Schema(description = "Top 10活跃用户")
  private Map<String, Long> topUsers;

  @Schema(description = "Top 10活跃仓库")
  private Map<String, Long> topRepositories;

  @Schema(description = "活动趋势（最近30天）")
  private List<ActivityTrendVo> activityTrend;
}
