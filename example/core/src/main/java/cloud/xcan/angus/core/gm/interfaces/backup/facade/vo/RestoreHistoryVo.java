package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import cloud.xcan.angus.core.gm.domain.backup.enums.RestoreStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "恢复历史记录")
public class RestoreHistoryVo {

  @Schema(description = "恢复时间")
  private LocalDateTime restoreTime;

  @Schema(description = "恢复人ID")
  private Long restoreBy;

  @Schema(description = "恢复人名称")
  private String restoreName;

  @Schema(description = "恢复状态")
  private RestoreStatus restoreStatus;
}
