package cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo;

import cloud.xcan.angus.core.repo.domain.activitylog.ActivityAction;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

/**
 * 活动日志视图对象
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "活动日志信息")
public class ActivityLogVo {

  @Schema(description = "租户ID")
  private String tenantId;

  @Schema(description = "创建人ID")
  private String createdBy;

  @Schema(description = "创建人姓名")
  private String creator;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "创建时间")
  private LocalDateTime createdDate;

  @Schema(description = "日志ID")
  private String id;

  @Schema(description = "操作类型")
  private ActivityAction action;

  @Schema(description = "操作用户")
  private String user;

  @Schema(description = "操作对象")
  private String artifact;

  @Schema(description = "仓库名称")
  private String repository;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "时间戳")
  private LocalDateTime timestamp;

  @Schema(description = "IP地址")
  private String ipAddress;

  @Schema(description = "User Agent")
  private String userAgent;

  @Schema(description = "详细信息")
  private String details;

  @Schema(description = "分类")
  private ActivityCategory category;
}
