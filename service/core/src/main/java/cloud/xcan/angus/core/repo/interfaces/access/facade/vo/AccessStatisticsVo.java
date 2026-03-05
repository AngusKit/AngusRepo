package cloud.xcan.angus.core.repo.interfaces.access.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "访问统计信息")
public class AccessStatisticsVo {

  @Schema(description = "规则总数")
  private Long totalRules;

  @Schema(description = "令牌总数")
  private Long totalTokens;

  @Schema(description = "访问总数")
  private Long totalAccesses;

  @Schema(description = "成功率（百分比）")
  private Double successRate;
}
