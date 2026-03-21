package cloud.xcan.angus.core.repo.interfaces.notification.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

import cloud.xcan.angus.core.repo.domain.notification.NotificationPriority;
import cloud.xcan.angus.core.repo.domain.notification.NotificationType;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "通知详情")
public class NotificationDetailVo extends TenantAuditingVo {

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

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "已读时间")
  private LocalDateTime readDate;
}
