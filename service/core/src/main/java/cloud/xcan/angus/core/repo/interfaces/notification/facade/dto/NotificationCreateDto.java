package cloud.xcan.angus.core.repo.interfaces.notification.facade.dto;

import cloud.xcan.angus.core.repo.domain.notification.NotificationPriority;
import cloud.xcan.angus.core.repo.domain.notification.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;
import static cloud.xcan.angus.core.repo.domain.Constants.*;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "创建通知请求参数")
public class NotificationCreateDto implements Serializable {

  @NotBlank
  @Length(max = MAX_LONG_DESC_LENGTH)
  @Schema(description = "通知标题", requiredMode = Schema.RequiredMode.REQUIRED)
  private String title;

  @Schema(description = "通知内容")
  private String message;

  @NotNull
  @Schema(description = "通知类型", requiredMode = Schema.RequiredMode.REQUIRED)
  private NotificationType type;

  @Schema(description = "优先级")
  private NotificationPriority priority = NotificationPriority.MEDIUM;

  @NotNull
  @Schema(description = "目标用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long targetUserId;

  @Schema(description = "来源ID")
  private String sourceId;

  @Schema(description = "来源类型")
  private String sourceType;

  @Schema(description = "操作链接")
  private String actionUrl;
}
