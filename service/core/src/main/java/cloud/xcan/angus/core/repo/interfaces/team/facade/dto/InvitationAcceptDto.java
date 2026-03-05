package cloud.xcan.angus.core.repo.interfaces.team.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "接受邀请请求参数")
public class InvitationAcceptDto {

  @NotBlank
  @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;
}
