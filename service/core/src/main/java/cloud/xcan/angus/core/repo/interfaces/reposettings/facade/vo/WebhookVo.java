package cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "Webhook详情")
public class WebhookVo implements Serializable {

  @Schema(description = "Webhook ID")
  private Long id;

  @Schema(description = "Webhook名称")
  private String name;

  @Schema(description = "Webhook URL")
  private String url;

  @Schema(description = "密钥")
  private String secret;

  @Schema(description = "是否启用")
  private Boolean active;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "最后触发时间")
  private LocalDateTime lastTriggerTime;

  @Schema(description = "成功次数")
  private Integer successCount;

  @Schema(description = "失败次数")
  private Integer failureCount;

  @Schema(description = "事件类型列表（JSON）")
  private String events;

  @Schema(description = "创建人ID")
  private Long createdBy;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "创建时间")
  private LocalDateTime createdDate;

  @Schema(description = "修改人ID")
  private Long modifiedBy;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;
}
