package cloud.xcan.angus.api.gm.user.dto;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "更新用户启用状态请求参数")
public class UserStatusUpdateDto {

  @NotNull
  @Schema(description = "启用状态", requiredMode = RequiredMode.REQUIRED)
  private EnabledStatus enableStatus;
}
