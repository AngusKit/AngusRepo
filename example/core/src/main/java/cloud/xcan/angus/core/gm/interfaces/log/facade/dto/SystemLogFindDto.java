package cloud.xcan.angus.core.gm.interfaces.log.facade.dto;

import cloud.xcan.angus.core.gm.domain.log.enums.LogStatus;
import cloud.xcan.angus.core.gm.domain.log.enums.LogType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统日志查询DTO")
public class SystemLogFindDto extends PageQuery {

  @Schema(description = "日志文件ID")
  private Long id;

  @Schema(description = "日志类型：APPLICATION/ERROR/CONSOLE/OTHER")
  private LogType type;

  @Schema(description = "应用ID")
  private String applicationId;

  @Schema(description = "开始日期 yyyy-MM-dd")
  private LocalDate startDate;

  @Schema(description = "结束日期 yyyy-MM-dd")
  private LocalDate endDate;

  @Schema(description = "状态：ACTIVE/COMPLETED/ARCHIVED")
  private LogStatus status;

  @Override
  public String getDefaultOrderBy() {
    return "date";
  }
}
