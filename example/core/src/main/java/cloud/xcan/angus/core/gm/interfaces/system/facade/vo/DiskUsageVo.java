package cloud.xcan.angus.core.gm.interfaces.system.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "磁盘使用情况")
public class DiskUsageVo {

  @Schema(description = "磁盘信息列表")
  private List<DiskInfo> disks;

  @Data
  @Schema(description = "磁盘信息")
  public static class DiskInfo {

    @Schema(description = "设备名称")
    private String device;

    @Schema(description = "挂载点")
    private String mountPoint;

    @Schema(description = "文件系统类型")
    private String fileSystem;

    @Schema(description = "总容量")
    private String total;

    @Schema(description = "已使用容量")
    private String used;

    @Schema(description = "可用容量")
    private String free;

    @Schema(description = "使用率(%)")
    private Double usagePercent;
  }
}
