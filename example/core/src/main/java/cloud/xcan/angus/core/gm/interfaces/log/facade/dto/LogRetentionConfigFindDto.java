package cloud.xcan.angus.core.gm.interfaces.log.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "日志清理配置查询DTO")
public class LogRetentionConfigFindDto {

  @Schema(description = "应用ID")
  private Long applicationId;

}
