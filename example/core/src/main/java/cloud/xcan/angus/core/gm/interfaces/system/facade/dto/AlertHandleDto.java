package cloud.xcan.angus.core.gm.interfaces.system.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "告警处理DTO")
public class AlertHandleDto {

  @NotBlank
  @Schema(description = "处理状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "已处理")
  private String status;

  @Schema(description = "处理备注", example = "已扩容服务器资源")
  private String handleNote;
}
