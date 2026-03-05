package cloud.xcan.angus.core.repo.interfaces.notification.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "通知统计信息")
public class NotificationStatisticsVo {

  @Schema(description = "总数")
  private Long totalCount;

  @Schema(description = "未读数")
  private Long unreadCount;

  @Schema(description = "安全通知数")
  private Long securityCount;

  @Schema(description = "存储通知数")
  private Long storageCount;

  @Schema(description = "访问通知数")
  private Long accessCount;

  @Schema(description = "制品通知数")
  private Long artifactCount;

  @Schema(description = "系统通知数")
  private Long systemCount;
}
