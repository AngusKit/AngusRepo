package cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "系统资源使用情况")
public class SystemResourcesVo {

  @Schema(description = "CPU使用情况")
  private ResourceUsageVo cpu;

  @Schema(description = "内存使用情况")
  private ResourceUsageVo memory;

  @Schema(description = "磁盘使用情况")
  private ResourceUsageVo disk;

  @Schema(description = "网络带宽使用情况")
  private ResourceUsageVo network;

  @Schema(description = "数据采集时间（ISO 8601格式）")
  private LocalDateTime collectedAt;

  @Data
  @Schema(description = "资源使用情况")
  public static class ResourceUsageVo {

    @Schema(description = "标签名称")
    private String label;

    @Schema(description = "使用百分比（0-100）")
    private Integer usagePercent;

    @Schema(description = "总容量描述")
    private String total;

    @Schema(description = "当前使用量描述")
    private String current;
  }
}
