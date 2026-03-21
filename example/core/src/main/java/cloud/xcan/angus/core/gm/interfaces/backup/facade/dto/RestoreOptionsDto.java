package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "恢复选项")
public class RestoreOptionsDto {

  @NotNull
  @Schema(description = "是否恢复数据库，默认打开", requiredMode = Schema.RequiredMode.REQUIRED)
  private Boolean restoreDatabase = true;

  @NotNull
  @Schema(description = "是否恢复配置文件 (应用`/conf`目录)，默认打开", requiredMode = Schema.RequiredMode.REQUIRED)
  private Boolean restoreConfig = true;

  @NotNull
  @Schema(description = "是否恢复文件数据 (应用`/data`目录)，默认打开", requiredMode = Schema.RequiredMode.REQUIRED)
  private Boolean restoreFiles = true;

  @NotNull
  @Schema(description = "是否恢复系统日志 (应用`/logs`目录)，默认关闭", requiredMode = Schema.RequiredMode.REQUIRED)
  private Boolean restoreLogs = false;

}
