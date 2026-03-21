package cloud.xcan.angus.core.gm.interfaces.quota.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "配额统计信息VO")
public class QuotaStatisticsVo implements Serializable {

  @Schema(description = "配额资源总数")
  private Integer totalResources;

  @Schema(description = "已应用配额数（启用且有上限限制）")
  private Integer appliedQuotas;

  @Schema(description = "配额不足数（使用率 >= 90%）")
  private Integer insufficientQuotas;

  @Schema(description = "已启用配额数")
  private Integer enabledQuotas;

  @Schema(description = "已禁用配额数")
  private Integer disabledQuotas;

  @Schema(description = "按应用统计")
  private List<AppStatistic> appStatistics;

  @Data
  @Schema(description = "应用统计")
  public static class AppStatistic implements Serializable {

    @Schema(description = "应用编码")
    private String appCode;

    @Schema(description = "配额数量")
    private Integer quotaCount;

    @Schema(description = "不足配额数")
    private Integer insufficientCount;
  }
}
