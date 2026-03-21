package cloud.xcan.angus.core.gm.interfaces.quota.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

@Data
@Schema(description = "更新配额状态DTO")
public class UpdateQuotaStatusDto implements Serializable {

  @NotNull
  @Schema(description = "启用状态", requiredMode = RequiredMode.REQUIRED)
  private Boolean enabled;
}
