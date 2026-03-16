package cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto;

import cloud.xcan.angus.core.repo.domain.activitylog.ActivityAction;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;
import java.io.Serializable;

/**
 * 导出活动日志DTO
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "导出活动日志请求参数")
public class ActivityLogExportDto implements Serializable {

  @Schema(description = "导出格式（csv/excel）", defaultValue = "csv")
  private String format = "csv";

  @Schema(description = "操作类型筛选")
  private ActivityAction action;

  @Schema(description = "用户筛选")
  private String user;

  @Schema(description = "仓库筛选")
  private String repository;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "开始时间")
  private LocalDateTime startDate;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "结束时间")
  private LocalDateTime endDate;
}
