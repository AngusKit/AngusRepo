package cloud.xcan.angus.api.gm;


import cloud.xcan.angus.api.commonlink.EnabledStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "更新状态请求参数")
public class EnabledStatusUpdateDto {

  @NotNull
  @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLED")
  private EnabledStatus status;
}
