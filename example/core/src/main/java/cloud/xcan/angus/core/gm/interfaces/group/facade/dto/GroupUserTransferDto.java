package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "转移组用户请求参数")
public class GroupUserTransferDto {

  @NotNull
  @Schema(description = "目标组ID", requiredMode = RequiredMode.REQUIRED)
  private Long targetGroupId;

  @NotEmpty
  @Schema(description = "用户ID列表", requiredMode = RequiredMode.REQUIRED)
  private List<Long> userIds;
}
