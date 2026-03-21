package cloud.xcan.angus.core.gm.interfaces.log.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "系统日志批量删除DTO")
public class SystemLogBatchDeleteDto {

  @NotEmpty
  @Schema(description = "日志文件ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<String> ids;

  @Schema(description = "是否永久删除，默认false（归档）")
  private Boolean permanent = false;
}
