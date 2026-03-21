package cloud.xcan.angus.core.gm.interfaces.system.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "CPU使用情况")
public class CpuUsageVo {

  @Schema(description = "当前CPU使用率(%)")
  private Double current;

  @Schema(description = "平均CPU使用率(%)")
  private Double average;

  @Schema(description = "最大CPU使用率(%)")
  private Double max;

  @Schema(description = "最小CPU使用率(%)")
  private Double min;

  @Schema(description = "CPU核心数")
  private Integer cores;

  @Schema(description = "CPU使用历史记录")
  private List<CpuHistory> history;

  @Data
  @Schema(description = "CPU使用历史记录")
  public static class CpuHistory {

    @Schema(description = "时间")
    private LocalDateTime time;

    @Schema(description = "CPU使用率(%)")
    private Double usage;
  }
}
