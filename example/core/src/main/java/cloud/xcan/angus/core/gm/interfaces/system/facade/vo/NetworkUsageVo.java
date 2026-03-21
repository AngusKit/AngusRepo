package cloud.xcan.angus.core.gm.interfaces.system.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "网络使用情况")
public class NetworkUsageVo {

  @Schema(description = "网络接口列表")
  private List<NetworkInterface> interfaces;

  @Schema(description = "网络使用历史记录")
  private List<NetworkHistory> history;

  @Data
  @Schema(description = "网络接口信息")
  public static class NetworkInterface {

    @Schema(description = "接口名称")
    private String name;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "接口状态")
    private String status;

    @Schema(description = "接收字节数")
    private String bytesIn;

    @Schema(description = "发送字节数")
    private String bytesOut;

    @Schema(description = "当前接收速率")
    private String currentInRate;

    @Schema(description = "当前发送速率")
    private String currentOutRate;
  }

  @Data
  @Schema(description = "网络使用历史记录")
  public static class NetworkHistory {

    @Schema(description = "时间")
    private LocalDateTime time;

    @Schema(description = "接收速率")
    private String inRate;

    @Schema(description = "发送速率")
    private String outRate;
  }
}
