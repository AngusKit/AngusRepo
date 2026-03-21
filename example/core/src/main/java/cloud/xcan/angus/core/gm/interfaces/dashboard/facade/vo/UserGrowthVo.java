package cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "用户增长趋势")
public class UserGrowthVo {

  @Schema(description = "查询的时间范围")
  private String timeRange;

  @Schema(description = "数据点列表")
  private List<GrowthDataPointVo> dataPoints;

  @Schema(description = "时间段内用户增长数")
  private Long totalGrowth;

  @Schema(description = "增长率")
  private String growthRate;

  @Data
  @Schema(description = "增长数据点")
  public static class GrowthDataPointVo {

    @Schema(description = "日期（ISO 8601格式）")
    private String date;

    @Schema(description = "显示标签（如\"7月\"、\"2024/1\"等）")
    private String label;

    @Schema(description = "该时间点的用户总数")
    private Long userCount;
  }
}
