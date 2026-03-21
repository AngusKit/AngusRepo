package cloud.xcan.angus.core.gm.interfaces.system.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "进程信息")
public class ProcessInfoVo {

  @Schema(description = "进程ID")
  private Long pid;

  @Schema(description = "进程名称")
  private String name;

  @Schema(description = "运行用户")
  private String user;

  @Schema(description = "CPU使用率(%)")
  private Double cpuPercent;

  @Schema(description = "内存使用率(%)")
  private Double memoryPercent;

  @Schema(description = "内存使用量")
  private String memoryUsage;

  @Schema(description = "进程状态")
  private String status;

  @Schema(description = "启动时间")
  private LocalDateTime startTime;

  @Schema(description = "启动命令")
  private String command;
}
