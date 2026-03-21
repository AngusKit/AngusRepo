package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "转移组用户响应")
public class GroupUserTransferVo {

  @Schema(description = "源组ID")
  private Long sourceGroupId;

  @Schema(description = "目标组ID")
  private Long targetGroupId;

  @Schema(description = "转移成功数量")
  private Integer transferredCount;
}
