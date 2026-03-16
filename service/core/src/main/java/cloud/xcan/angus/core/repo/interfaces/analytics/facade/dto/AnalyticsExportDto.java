package cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "分析报告导出参数")
public class AnalyticsExportDto implements Serializable {

  @Schema(description = "导出格式(PDF/EXCEL)")
  private String exportFormat = "EXCEL";

  @Schema(description = "开始日期")
  private LocalDate startDate;

  @Schema(description = "结束日期")
  private LocalDate endDate;

  @Schema(description = "仓库ID筛选")
  private Long repositoryId;
}
