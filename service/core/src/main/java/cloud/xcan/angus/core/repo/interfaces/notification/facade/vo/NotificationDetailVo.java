package cloud.xcan.angus.core.repo.interfaces.notification.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

import cloud.xcan.angus.core.repo.domain.notification.NotificationPriority;
import cloud.xcan.angus.core.repo.domain.notification.NotificationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "通知详情")
public class NotificationDetailVo implements Serializable {

  @Schema(description = "通知ID")
  private String id;

  @Schema(description = "标题")
  private String title;

  @Schema(description = "内容")
  private String message;

  @Schema(description = "类型")
  private NotificationType type;

  @Schema(description = "优先级")
  private NotificationPriority priority;

  @Schema(description = "是否已读")
  private Boolean isRead;

  @Schema(description = "是否星标")
  private Boolean isStarred;

  @Schema(description = "是否归档")
  private Boolean isArchived;

  @Schema(description = "目标用户ID")
  private Long targetUserId;

  @Schema(description = "来源ID")
  private String sourceId;

  @Schema(description = "来源类型")
  private String sourceType;

  @Schema(description = "操作链接")
  private String actionUrl;

  @Schema(description = "创建人ID")
  private Long createdBy;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "创建时间")
  private LocalDateTime createdDate;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "已读时间")
  private LocalDateTime readDate;
}
