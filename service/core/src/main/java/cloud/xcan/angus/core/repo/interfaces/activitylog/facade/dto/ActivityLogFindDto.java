package cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto;

import cloud.xcan.angus.core.repo.domain.activitylog.ActivityAction;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityCategory;
import cloud.xcan.angus.remote.PageQuery;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

/**
 * 查询活动日志列表DTO
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "查询活动日志列表请求参数")
public class ActivityLogFindDto extends PageQuery {

  @Schema(description = "搜索关键词（制品、用户、仓库）")
  private String search;

  @Schema(description = "操作类型筛选")
  private ActivityAction action;

  @Schema(description = "用户筛选")
  private String user;

  @Schema(description = "仓库筛选")
  private String repository;

  @Schema(description = "分类筛选")
  private ActivityCategory category;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "开始时间")
  private LocalDateTime startDate;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "结束时间")
  private LocalDateTime endDate;

  @Override
  public String getDefaultOrderBy() {
    return "timestamp";
  }
}
