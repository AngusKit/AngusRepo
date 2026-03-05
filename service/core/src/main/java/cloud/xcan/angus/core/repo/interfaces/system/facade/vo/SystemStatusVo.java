package cloud.xcan.angus.core.repo.interfaces.system.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "系统状态信息")
public class SystemStatusVo {

  @Schema(description = "系统版本")
  private String version;

  @Schema(description = "运行时间")
  private String uptime;

  @Schema(description = "JVM内存使用（字节）")
  private Long memoryUsed;

  @Schema(description = "JVM内存总量（字节）")
  private Long memoryTotal;

  @Schema(description = "磁盘使用（字节）")
  private Long diskUsed;

  @Schema(description = "磁盘总量（字节）")
  private Long diskTotal;

  @Schema(description = "数据库状态")
  private String databaseStatus;

  @Schema(description = "搜索引擎状态")
  private String searchStatus;
}
