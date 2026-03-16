package cloud.xcan.angus.core.repo.interfaces.team.facade.dto;

import cloud.xcan.angus.core.repo.domain.team.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "邀请成员请求参数")
public class MemberInviteDto implements Serializable {

  @NotBlank
  @Email
  @Schema(description = "邮箱地址", requiredMode = Schema.RequiredMode.REQUIRED)
  private String email;

  @NotNull
  @Schema(description = "成员角色", requiredMode = Schema.RequiredMode.REQUIRED)
  private UserRole role;

  @Schema(description = "邀请消息")
  private String message;
}
