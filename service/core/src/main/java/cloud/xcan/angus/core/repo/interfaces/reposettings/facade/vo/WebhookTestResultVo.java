package cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "Webhook测试结果")
public class WebhookTestResultVo implements Serializable {

  @Schema(description = "是否成功")
  private Boolean success;

  @Schema(description = "HTTP状态码")
  private Integer statusCode;

  @Schema(description = "响应时间（毫秒）")
  private Long responseTime;

  @Schema(description = "响应内容")
  private String response;
}
