package cloud.xcan.angus.core.repo.interfaces.team.facade.dto;

import cloud.xcan.angus.core.repo.domain.team.MemberStatus;
import cloud.xcan.angus.core.repo.domain.team.UserRole;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "查询团队成员列表请求参数")
public class TeamMemberFindDto extends PageQuery {

  @Schema(description = "角色筛选")
  private UserRole role;

  @Schema(description = "状态筛选")
  private MemberStatus status;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
