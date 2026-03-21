package cloud.xcan.angus.core.gm.interfaces.system.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "健康检查结果")
public class HealthCheckVo {

  @Schema(description = "系统状态")
  private String status;

  @Schema(description = "组件健康状态")
  private Map<String, ComponentHealth> components;

  @Data
  @Schema(description = "组件健康状态")
  public static class ComponentHealth {

    @Schema(description = "组件状态")
    private String status;

    @Schema(description = "响应时间(毫秒)")
    private Integer responseTime;

    @Schema(description = "详细信息")
    private Map<String, Object> details;
  }
}
