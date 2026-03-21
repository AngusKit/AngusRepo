package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Data
@Schema(description = "响应时间TOP接口VO")
public class TopSlowVo implements Serializable {

  @Schema(description = "服务名称")
  private String serviceName;

  @Schema(description = "接口路径")
  private String uri;

  @Schema(description = "请求方法")
  private String method;

  @Schema(description = "平均响应时间（毫秒）")
  private Integer avgResponseTime;

  @Schema(description = "调用次数")
  private Long calls;
}
