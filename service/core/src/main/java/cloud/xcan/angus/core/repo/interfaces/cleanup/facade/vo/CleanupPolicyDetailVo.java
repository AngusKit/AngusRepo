package cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

import cloud.xcan.angus.core.repo.domain.cleanup.CleanupType;import cloud.xcan.angus.remote.NameJoinField;import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "清理策略详情")
public class CleanupPolicyDetailVo implements Serializable {

  @Schema(description = "策略ID")
  private String id;

  @Schema(description = "策略名称")
  private String name;

  @Schema(description = "策略描述")
  private String description;

  @Schema(description = "仓库ID")
  private String repositoryId;

  @Schema(description = "仓库名称")
  private String repositoryName;

  @Schema(description = "清理类型")
  private CleanupType type;

  @Schema(description = "是否启用")
  private Boolean enabled;

  @Schema(description = "是否试运行")
  private Boolean dryRun;

  @Schema(description = "清理条件JSON")
  private String conditionJson;

  @Schema(description = "调度配置JSON")
  private String scheduleJson;

  @Schema(description = "上次执行统计JSON")
  private String lastExecutionStatsJson;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "上次执行时间")
  private LocalDateTime lastExecuted;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "下次执行时间")
  private LocalDateTime nextExecution;

  @Schema(description = "执行次数")
  private Integer executionCount;

  @Schema(description = "创建人ID")
  private Long createdBy;

  @Schema(description = "创建人名称")
  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "创建时间")
  private LocalDateTime createdDate;

  @Schema(description = "修改人ID")
  private Long modifiedBy;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;
}
