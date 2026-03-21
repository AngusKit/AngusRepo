package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import cloud.xcan.angus.core.gm.domain.user.enums.NotificationChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "批量更新通知类型设置请求参数")
public class BatchUpdateNotificationDto {

  @NotNull
  @Schema(description = "通知渠道", requiredMode = Schema.RequiredMode.REQUIRED)
  private NotificationChannel channel;

  @NotNull
  @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED)
  private Boolean enabled;
}
