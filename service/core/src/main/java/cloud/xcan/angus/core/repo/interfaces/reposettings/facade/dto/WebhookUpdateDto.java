package cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新Webhook请求参数")
public class WebhookUpdateDto {

  @Size(max = 255)
  @Schema(description = "Webhook名称")
  private String name;

  @Size(max = 2000)
  @Schema(description = "Webhook URL")
  private String url;

  @Schema(description = "事件类型列表（JSON）")
  private String events;

  @Size(max = 255)
  @Schema(description = "密钥")
  private String secret;

  @Schema(description = "是否启用")
  private Boolean active;
}
