package cloud.xcan.angus.core.repo.interfaces.team.facade.vo;

import cloud.xcan.angus.core.repo.domain.team.MemberStatus;
import cloud.xcan.angus.core.repo.domain.team.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "团队成员信息")
public class TeamMemberVo {

  @Schema(description = "成员ID")
  private Long id;

  @Schema(description = "用户ID")
  private Long userId;

  @Schema(description = "成员名称")
  private String name;

  @Schema(description = "邮箱")
  private String email;

  @Schema(description = "头像")
  private String avatar;

  @Schema(description = "角色")
  private UserRole role;

  @Schema(description = "状态")
  private MemberStatus status;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "加入日期")
  private LocalDateTime joinedDate;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "最后活跃时间")
  private LocalDateTime lastActive;
}
