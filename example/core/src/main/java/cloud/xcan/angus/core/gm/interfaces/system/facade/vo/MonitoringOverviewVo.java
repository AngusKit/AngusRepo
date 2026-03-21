package cloud.xcan.angus.core.gm.interfaces.system.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "监控概览")
public class MonitoringOverviewVo {

  @Schema(description = "系统状态")
  private String systemStatus;

  @Schema(description = "CPU使用率(%)")
  private Double cpuUsage;

  @Schema(description = "内存使用率(%)")
  private Double memoryUsage;

  @Schema(description = "磁盘使用率(%)")
  private Double diskUsage;

  @Schema(description = "网络入站流量速率，如 1.23 MB/s")
  private String networkIn;

  @Schema(description = "网络出站流量速率，如 1.23 MB/s")
  private String networkOut;

  @Schema(description = "活跃连接数")
  private Long activeConnections;

  @Schema(description = "运行时长")
  private String uptime;

  @Schema(description = "最后更新时间")
  private LocalDateTime lastUpdateTime;
}
