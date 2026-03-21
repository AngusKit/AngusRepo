package cloud.xcan.angus.core.gm.interfaces.quota.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "批量更新配额限额DTO")
public class BatchUpdateQuotaLimitsDto implements Serializable {

  @NotNull
  @Size(min = 1)
  @Valid
  @Schema(description = "配额限额列表", requiredMode = RequiredMode.REQUIRED)
  private List<QuotaLimitDto> quotas;

  @Data
  @Schema(description = "配额限额DTO")
  public static class QuotaLimitDto implements Serializable {

    @NotNull
    @Schema(description = "配额编码", requiredMode = RequiredMode.REQUIRED)
    private String code;

    @NotNull
    @Schema(description = "配额限额", requiredMode = RequiredMode.REQUIRED)
    private Long limit;
  }
}
