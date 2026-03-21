package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import cloud.xcan.angus.core.gm.domain.backup.enums.RestoreStatus;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询恢复任务请求参数")
public class RestoreFindDto extends PageQuery {

  @Schema(description = "恢复状态筛选")
  private RestoreStatus status;

  @Schema(description = "开始日期")
  private LocalDateTime startDate;

  @Schema(description = "结束日期")
  private LocalDateTime endDate;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
