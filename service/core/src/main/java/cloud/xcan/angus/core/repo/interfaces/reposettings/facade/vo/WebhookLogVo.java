package cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "Webhook日志详情")
public class WebhookLogVo {

  @Schema(description = "日志ID")
  private Long id;

  @Schema(description = "Webhook ID")
  private Long webhookId;

  @Schema(description = "事件类型")
  private String event;

  @Schema(description = "HTTP状态码")
  private Integer statusCode;

  @Schema(description = "是否成功")
  private Boolean success;

  @Schema(description = "请求内容")
  private String request;

  @Schema(description = "响应内容")
  private String response;

  @Schema(description = "响应时间（毫秒）")
  private Long responseTime;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "触发时间")
  private LocalDateTime triggeredAt;
}
