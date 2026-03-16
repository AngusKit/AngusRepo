package cloud.xcan.angus.core.repo.interfaces.team.facade.dto;

import cloud.xcan.angus.core.repo.domain.team.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新成员角色请求参数")
public class MemberRoleUpdateDto implements Serializable {

  @NotNull
  @Schema(description = "成员角色", requiredMode = Schema.RequiredMode.REQUIRED)
  private UserRole role;
}
