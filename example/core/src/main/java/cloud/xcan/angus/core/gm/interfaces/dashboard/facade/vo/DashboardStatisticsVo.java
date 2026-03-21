package cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo;

import cloud.xcan.angus.api.commonlink.TrendEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "概览统计数据")
public class DashboardStatisticsVo {

  @Schema(description = "用户统计数据")
  private UserStatsVo userStats;

  @Schema(description = "租户统计数据")
  private TenantStatsVo tenantStats;

  @Schema(description = "操作统计数据")
  private OperationStatsVo operationStats;

  @Schema(description = "通知统计数据")
  private NotificationStatsVo notificationStats;

  @Data
  @Schema(description = "用户统计数据")
  public static class UserStatsVo {

    @Schema(description = "用户总数")
    private Long total;

    @Schema(description = "禁用用户数")
    private Long disabledCount;

    @Schema(description = "在线用户数")
    private Long onlineCount;

    @Schema(description = "环比变化率（如\"+12.5%\"）")
    private String changeRate;

    @Schema(description = "趋势方向：UP（上升）、DOWN（下降）、FLAT（持平）")
    private TrendEnum trend;
  }

  @Data
  @Schema(description = "租户统计数据")
  public static class TenantStatsVo {

    @Schema(description = "租户总数")
    private Long total;

    @Schema(description = "启用租户数")
    private Long enabledCount;

    @Schema(description = "禁用租户数")
    private Long disabledCount;

    @Schema(description = "环比变化率")
    private String changeRate;

    @Schema(description = "趋势方向")
    private TrendEnum trend;
  }

  @Data
  @Schema(description = "操作统计数据")
  public static class OperationStatsVo {

    @Schema(description = "操作总数")
    private Long total;

    @Schema(description = "异常操作数")
    private Long errorCount;

    @Schema(description = "成功操作数")
    private Long successCount;

    @Schema(description = "环比变化率")
    private String changeRate;

    @Schema(description = "趋势方向")
    private TrendEnum trend;
  }

  @Data
  @Schema(description = "通知统计数据")
  public static class NotificationStatsVo {

    @Schema(description = "通知总数")
    private Long total;

    @Schema(description = "站内通知数")
    private Long internalCount;

    @Schema(description = "邮件通知数")
    private Long emailCount;

    @Schema(description = "短信通知数")
    private Long smsCount;

    @Schema(description = "环比变化率")
    private String changeRate;

    @Schema(description = "趋势方向")
    private TrendEnum trend;
  }

}
