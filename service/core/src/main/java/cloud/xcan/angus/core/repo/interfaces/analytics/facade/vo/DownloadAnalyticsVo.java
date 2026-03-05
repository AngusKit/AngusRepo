package cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "下载分析结果")
public class DownloadAnalyticsVo {

  @Schema(description = "总下载次数")
  private Long totalDownloads;

  @Schema(description = "日均下载次数")
  private Long averageDailyDownloads;

  @Schema(description = "峰值下载次数")
  private Long peakDownloads;

  @Schema(description = "趋势数据")
  private List<TrendDataPointVo> trendData;
}
