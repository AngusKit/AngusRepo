package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "组统计数据")
public class GroupStatsVo {

  @Schema(description = "总组数")
  private Long totalGroups;

  @Schema(description = "项目组数")
  private Long projectGroups;

  @Schema(description = "职能组数")
  private Long functionGroups;

  @Schema(description = "临时组数")
  private Long tempGroups;

  @Schema(description = "活跃成员数")
  private Long activeMembers;

  @Schema(description = "本月新增组数")
  private Long newGroupsThisMonth;

  @Schema(description = "组用户数量分布")
  private List<GroupUsersVo> groupUsers;

  @Data
  @Schema(description = "组用户数量分布")
  public static class GroupUsersVo {

    @Schema(description = "组名称")
    private String groupName;

    @Schema(description = "用户数量")
    private Long userCount;
  }
}
