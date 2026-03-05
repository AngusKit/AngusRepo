package cloud.xcan.angus.core.repo.interfaces.notification.facade.dto;

import cloud.xcan.angus.core.repo.domain.notification.NotificationType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "查询通知列表请求参数")
public class NotificationFindDto extends PageQuery {

  @Schema(description = "通知类型筛选")
  private NotificationType type;

  @Schema(description = "是否已读筛选")
  private Boolean isRead;

  @Schema(description = "是否星标筛选")
  private Boolean isStarred;

  @Schema(description = "是否归档筛选")
  private Boolean isArchived;

  @Schema(description = "搜索关键词")
  private String search;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
