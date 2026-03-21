package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "Eureka连接测试结果")
public class EurekaTestVo {

  @Schema(description = "是否连接成功")
  private Boolean connected;

  @Schema(description = "响应时间（毫秒）")
  private Integer responseTime;

  @Schema(description = "服务数量")
  private Integer servicesCount;
}
