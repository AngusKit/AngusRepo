package cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "系统指标")
public class SystemMetricsVo implements Serializable {

  @Schema(description = "JVM最大内存(字节)")
  private Long jvmMaxMemory;

  @Schema(description = "JVM已用内存(字节)")
  private Long jvmUsedMemory;

  @Schema(description = "JVM空闲内存(字节)")
  private Long jvmFreeMemory;

  @Schema(description = "磁盘总空间(字节)")
  private Long diskTotalSpace;

  @Schema(description = "磁盘可用空间(字节)")
  private Long diskFreeSpace;

  @Schema(description = "系统CPU使用率(%)")
  private Double cpuUsage;

  @Schema(description = "系统运行时间(秒)")
  private Long uptimeSeconds;

  @Schema(description = "可用处理器数")
  private Integer availableProcessors;
}
