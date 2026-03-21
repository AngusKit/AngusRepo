package cloud.xcan.angus.core.gm.interfaces.log.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "执行日志清理DTO")
public class LogRetentionConfigCleanupDto {

  @Schema(description = "是否仅模拟运行，默认false")
  private Boolean dryRun = false;
}
