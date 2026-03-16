package cloud.xcan.angus.core.repo.interfaces.notification.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "批量标记已读请求参数")
public class NotificationBatchReadDto implements Serializable {

  @NotNull
  @Size(min = 1)
  @Schema(description = "通知ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<String> ids;
}
