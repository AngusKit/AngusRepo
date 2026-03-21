package cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo;

import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "最近活动列表")
public class RecentActivitiesVo {

  @Schema(description = "活动记录列表")
  private List<ActivityVo> activities;

  @Schema(description = "总活动数")
  private Long total;

  @Data
  @Schema(description = "活动记录")
  public static class ActivityVo {

    @Schema(description = "活动记录ID")
    private Long id;

    @Schema(description = "活动类型")
    private ResourceType type;

    @Schema(description = "活动标题")
    private String title;

    @Schema(description = "活动描述")
    private String description;

    @Schema(description = "相关用户ID（可选）")
    private Long relatedUserId;

    @Schema(description = "相关用户名称（可选）")
    private String relatedUserName;

    @Schema(description = "相关租户ID（可选）")
    private Long relatedTenantId;

    @Schema(description = "相关租户名称（可选）")
    private String relatedTenantName;

    @Schema(description = "发生时间（ISO 8601格式）")
    private LocalDateTime occurredAt;

  }

}
