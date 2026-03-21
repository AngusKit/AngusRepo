package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "服务实例")
public class ServiceInstanceVo {

  @Schema(description = "实例ID")
  private String instanceId;

  @Schema(description = "主机名")
  private String hostName;

  @Schema(description = "IP地址")
  private String ipAddr;

  @Schema(description = "端口")
  private Integer port;

  @Schema(description = "安全端口")
  private Integer securePort;

  @Schema(description = "状态（UP, DOWN, OUT_OF_SERVICE）")
  private String status;

  @Schema(description = "健康检查URL")
  private String healthCheckUrl;

  @Schema(description = "状态页面URL")
  private String statusPageUrl;

  @Schema(description = "主页URL")
  private String homePageUrl;

  @Schema(description = "最后心跳时间")
  private LocalDateTime lastHeartbeat;

  @Schema(description = "运行时长")
  private String uptime;

  @Schema(description = "元数据")
  private Map<String, String> metadata;
}
