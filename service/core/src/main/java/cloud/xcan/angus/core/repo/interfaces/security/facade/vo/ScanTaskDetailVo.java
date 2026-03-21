package cloud.xcan.angus.core.repo.interfaces.security.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

import cloud.xcan.angus.core.repo.domain.security.ScanStatus;
import cloud.xcan.angus.core.repo.domain.security.ScanType;import cloud.xcan.angus.remote.NameJoinField;import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "扫描任务详情")
public class ScanTaskDetailVo implements Serializable {

  @Schema(description = "任务ID")
  private String id;

  @Schema(description = "制品ID")
  private String artifactId;

  @Schema(description = "制品名称")
  private String artifactName;

  @Schema(description = "仓库ID")
  private String repositoryId;

  @Schema(description = "仓库名称")
  private String repositoryName;

  @Schema(description = "扫描类型")
  private ScanType scanType;

  @Schema(description = "状态")
  private ScanStatus status;

  @Schema(description = "进度")
  private Integer progress;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "开始时间")
  private LocalDateTime startTime;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "结束时间")
  private LocalDateTime endTime;

  @Schema(description = "执行时长(秒)")
  private Long durationSeconds;

  @Schema(description = "漏洞总数")
  private Integer vulnerabilityCount;

  @Schema(description = "严重漏洞数")
  private Integer criticalCount;

  @Schema(description = "高危漏洞数")
  private Integer highCount;

  @Schema(description = "中危漏洞数")
  private Integer mediumCount;

  @Schema(description = "低危漏洞数")
  private Integer lowCount;

  @Schema(description = "错误信息")
  private String errorMessage;

  @Schema(description = "创建人ID")
  private Long createdBy;

  @Schema(description = "创建人名称")
  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "创建时间")
  private LocalDateTime createdDate;
}
