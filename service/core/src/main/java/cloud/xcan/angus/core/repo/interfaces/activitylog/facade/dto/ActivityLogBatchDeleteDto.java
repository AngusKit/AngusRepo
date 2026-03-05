package cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto;

import cloud.xcan.angus.core.repo.domain.activitylog.ActivityCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

/**
 * 批量删除活动日志DTO
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "批量删除活动日志请求参数")
public class ActivityLogBatchDeleteDto {

  @Schema(description = "日志ID列表（为空则按条件删除）")
  private List<String> ids;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "删除此日期之前的日志")
  private LocalDateTime beforeDate;

  @Schema(description = "按分类删除")
  private ActivityCategory category;
}
