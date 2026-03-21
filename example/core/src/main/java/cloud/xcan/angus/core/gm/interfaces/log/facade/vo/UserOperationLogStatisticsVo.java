package cloud.xcan.angus.core.gm.interfaces.log.facade.vo;

import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "用户操作日志统计数据")
public class UserOperationLogStatisticsVo {

  @Schema(description = "总操作次数")
  private Long totalCount;

  @Schema(description = "总操作次数较上周增长率(%)")
  private Double totalCountGrowthRate;

  @Schema(description = "成功操作次数")
  private Long successCount;

  @Schema(description = "成功操作次数较上周增长率(%)")
  private Double successCountGrowthRate;

  @Schema(description = "失败操作次数")
  private Long errorCount;

  @Schema(description = "失败操作次数较上周增长率(%)")
  private Double errorCountGrowthRate;

  @Schema(description = "成功率（百分比）")
  private Double successRate;

  @Schema(description = "成功率较上周增长率(%)")
  private Double successRateGrowthRate;

  @Schema(description = "各操作类型统计")
  private Map<OperationAction, Long> actionStatistics;

  @Schema(description = "各资源类型统计")
  private Map<ResourceType, Long> resourceStatistics;

  @Schema(description = "操作最频繁的用户TOP10")
  private List<TopUserVo> topUsers;

  @Data
  @Schema(description = "操作最频繁的用户")
  public static class TopUserVo {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "操作次数")
    private Long operationCount;
  }
}
