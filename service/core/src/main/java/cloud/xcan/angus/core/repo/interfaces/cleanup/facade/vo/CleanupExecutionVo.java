package cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

import cloud.xcan.angus.core.repo.domain.cleanup.CleanupStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "清理执行记录")
public class CleanupExecutionVo {

  @Schema(description = "执行ID")
  private String id;

  @Schema(description = "策略ID")
  private String policyId;

  @Schema(description = "策略名称")
  private String policyName;

  @Schema(description = "执行状态")
  private CleanupStatus status;

  @Schema(description = "执行进度")
  private Integer progress;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "开始时间")
  private LocalDateTime startTime;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "结束时间")
  private LocalDateTime endTime;

  @Schema(description = "执行时长（秒）")
  private Long durationSeconds;

  @Schema(description = "错误信息")
  private String errorMessage;

  @Schema(description = "统计信息JSON")
  private String statisticsJson;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "创建时间")
  private LocalDateTime createdDate;
}
