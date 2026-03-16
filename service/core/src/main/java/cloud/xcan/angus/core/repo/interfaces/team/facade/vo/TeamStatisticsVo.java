package cloud.xcan.angus.core.repo.interfaces.team.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "团队统计信息")
public class TeamStatisticsVo implements Serializable {

  @Schema(description = "总成员数")
  private Long totalMembers;

  @Schema(description = "活跃成员数")
  private Long activeMembers;

  @Schema(description = "管理员数量")
  private Long adminCount;

  @Schema(description = "开发者数量")
  private Long developerCount;

  @Schema(description = "查看者数量")
  private Long viewerCount;

  @Schema(description = "待处理邀请数")
  private Long pendingInvitations;
}
