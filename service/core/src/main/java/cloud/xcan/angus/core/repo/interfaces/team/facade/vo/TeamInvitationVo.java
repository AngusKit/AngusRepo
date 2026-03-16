package cloud.xcan.angus.core.repo.interfaces.team.facade.vo;

import cloud.xcan.angus.core.repo.domain.team.InvitationStatus;
import cloud.xcan.angus.core.repo.domain.team.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "团队邀请信息")
public class TeamInvitationVo implements Serializable {

  @Schema(description = "邀请ID")
  private Long id;

  @Schema(description = "邮箱")
  private String email;

  @Schema(description = "角色")
  private UserRole role;

  @Schema(description = "邀请状态")
  private InvitationStatus status;

  @Schema(description = "邀请消息")
  private String message;

  @Schema(description = "邀请人ID")
  private Long invitedBy;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "邀请日期")
  private LocalDateTime invitedDate;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "过期时间")
  private LocalDateTime expiresAt;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "接受时间")
  private LocalDateTime acceptedDate;
}
