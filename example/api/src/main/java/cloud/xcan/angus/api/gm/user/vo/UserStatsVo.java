package cloud.xcan.angus.api.gm.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "用户统计数据")
public class UserStatsVo {

  @Schema(description = "总用户数")
  private Long totalUsers;

  @Schema(description = "总用户数变化量")
  private Long totalUsersChange;

  @Schema(description = "已激活用户数")
  private Long activeUsers;

  @Schema(description = "活跃用户增长率(%)")
  private Double activeUsersGrowthRate;

  @Schema(description = "已禁用用户数")
  private Long disabledUsers;

  @Schema(description = "待审核用户数")
  private Long pendingUsers;

  @Schema(description = "在线用户数")
  private Long onlineUsers;

  @Schema(description = "管理员数量")
  private Long adminUsers;

  @Schema(description = "管理员数量变化量")
  private Long adminUsersChange;

  @Schema(description = "本月新增用户数")
  private Long newUsersThisMonth;

  @Schema(description = "本月新增用户增长率(%)")
  private Double newUsersGrowthRate;

  @Schema(description = "部门人员分布")
  private List<DepartmentDistributionVo> departmentDistribution;

  @Schema(description = "用户增长趋势（过去6个月）")
  private List<UserGrowthTrendVo> growthTrend;

  @Schema(description = "待接收邀请数")
  private Long pendingInvites;

  @Schema(description = "过去7天活跃率(%)")
  private Double activeRate7Days;

  @Data
  @Schema(description = "部门人员分布")
  public static class DepartmentDistributionVo {

    @Schema(description = "部门名称")
    private String departmentName;

    @Schema(description = "用户数量")
    private Long userCount;

    @Schema(description = "占比(%)")
    private Double percentage;
  }

  @Data
  @Schema(description = "用户增长趋势")
  public static class UserGrowthTrendVo {

    @Schema(description = "月份（如：1月）")
    private String month;

    @Schema(description = "用户总数")
    private Long totalCount;
  }
}
