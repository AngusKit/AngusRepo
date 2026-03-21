package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "错误率TOP接口VO")
public class TopErrorsVo implements Serializable {

  @Schema(description = "服务名称")
  private String serviceName;

  @Schema(description = "接口路径")
  private String uri;

  @Schema(description = "请求方法")
  private String method;

  @Schema(description = "错误率")
  private Double errorRate;

  @Schema(description = "调用次数")
  private Long calls;

  @Schema(description = "失败次数")
  private Long failedCalls;

  @Schema(description = "最后错误，取自最后一次错误调用的 responseBody JSON 第一层 message 字段")
  private String lastError;

  @Schema(description = "最后错误发生时间")
  private LocalDateTime lastErrorTime;
}
