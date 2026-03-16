package cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "创建Webhook请求参数")
public class WebhookCreateDto implements Serializable {

  @NotBlank
  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "Webhook名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Size(max = 2000)
  @Schema(description = "Webhook URL", requiredMode = Schema.RequiredMode.REQUIRED)
  private String url;

  @Schema(description = "事件类型列表（JSON）")
  private String events;

  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "密钥")
  private String secret;

  @Schema(description = "是否启用")
  private Boolean active;
}
