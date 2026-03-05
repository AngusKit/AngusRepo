package cloud.xcan.angus.core.repo.interfaces.notification.facade.dto;

import cloud.xcan.angus.core.repo.domain.notification.NotificationPriority;
import cloud.xcan.angus.core.repo.domain.notification.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新通知请求参数")
public class NotificationUpdateDto {

  @Size(max = 500)
  @Schema(description = "通知标题")
  private String title;

  @Schema(description = "通知内容")
  private String message;

  @Schema(description = "通知类型")
  private NotificationType type;

  @Schema(description = "优先级")
  private NotificationPriority priority;
}
