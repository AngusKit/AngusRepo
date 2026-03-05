package cloud.xcan.angus.core.repo.interfaces.security.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "扫描统计信息")
public class ScanStatisticsVo {

  @Schema(description = "总扫描数")
  private Long totalScans;

  @Schema(description = "已完成扫描数")
  private Long completedScans;

  @Schema(description = "失败扫描数")
  private Long failedScans;

  @Schema(description = "运行中扫描数")
  private Long runningScans;

  @Schema(description = "等待中扫描数")
  private Long pendingScans;

  @Schema(description = "总漏洞数")
  private Long totalVulnerabilities;
}
