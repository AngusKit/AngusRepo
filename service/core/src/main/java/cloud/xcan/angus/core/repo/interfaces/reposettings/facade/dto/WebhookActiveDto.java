package cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "启用/禁用Webhook请求参数")
public class WebhookActiveDto {

  @NotNull
  @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED)
  private Boolean active;
}
