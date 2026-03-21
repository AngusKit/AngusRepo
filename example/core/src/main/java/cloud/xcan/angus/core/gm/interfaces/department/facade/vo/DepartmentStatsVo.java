package cloud.xcan.angus.core.gm.interfaces.department.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "部门统计数据")
public class DepartmentStatsVo {

  @Schema(description = "总部门数")
  private Long totalDepartments;

  @Schema(description = "总部门数变化量")
  private Long totalDepartmentsChange;

  @Schema(description = "已启用部门数")
  private Long enabledDepartments;

  @Schema(description = "已禁用部门数")
  private Long disabledDepartments;

  @Schema(description = "总用户数")
  private Long totalUsers;

  @Schema(description = "最大层级深度")
  private Integer maxLevel;

  @Schema(description = "本月新增部门数")
  private Long newDepartmentsThisMonth;

  @Schema(description = "一级部门数量")
  private Long firstLevelDepartments;

  @Schema(description = "一级部门数量变化量")
  private Long firstLevelDepartmentsChange;

  @Schema(description = "平均人数")
  private Double averageUsersPerDepartment;

  @Schema(description = "平均人数增长率(%)")
  private Double averageUsersGrowthRate;

  @Schema(description = "部门规模分布")
  private List<DepartmentSizeDistributionVo> sizeDistribution;

  @Schema(description = "部门层级分布")
  private List<DepartmentLevelDistributionVo> levelDistribution;

  @Data
  @Schema(description = "部门规模分布")
  public static class DepartmentSizeDistributionVo {

    @Schema(description = "规模范围（如：10人以下）")
    private String sizeRange;

    @Schema(description = "部门数量")
    private Long count;
  }

  @Data
  @Schema(description = "部门层级分布")
  public static class DepartmentLevelDistributionVo {

    @Schema(description = "层级（如：一级部门）")
    private String levelName;

    @Schema(description = "部门数量")
    private Long count;
  }
}
