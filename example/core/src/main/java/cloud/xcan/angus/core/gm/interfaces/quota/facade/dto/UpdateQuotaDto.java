package cloud.xcan.angus.core.gm.interfaces.quota.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

@Data
@Schema(description = "更新资源配额DTO")
public class UpdateQuotaDto implements Serializable {

  @NotBlank
  @Schema(description = "配额编码", requiredMode = RequiredMode.REQUIRED)
  private String code;

  @NotNull
  @Min(value = 0)
  @Schema(description = "配额限额", requiredMode = RequiredMode.REQUIRED)
  private Long limit;

  @NotBlank
  @Schema(description = "单位", requiredMode = RequiredMode.REQUIRED)
  private String unit;

  @Schema(description = "资源说明")
  private String description;

  @Schema(description = "图标标识")
  private String icon;
}
