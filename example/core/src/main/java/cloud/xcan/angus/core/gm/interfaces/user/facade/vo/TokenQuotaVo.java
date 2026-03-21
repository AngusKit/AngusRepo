package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "令牌配额统计")
public class TokenQuotaVo {

  @Schema(description = "总配额", example = "10")
  private Integer total;

  @Schema(description = "已使用", example = "4")
  private Integer used;

  @Schema(description = "可用", example = "6")
  private Integer available;

  @Schema(description = "活跃数量", example = "3")
  private Integer activeCount;

  @Schema(description = "过期数量", example = "1")
  private Integer expiredCount;

  @Schema(description = "撤销数量", example = "0")
  private Integer revokedCount;
}
