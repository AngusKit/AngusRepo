package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Data
@Accessors(chain = true)
@Schema(description = "拒绝邀请请求参数")
public class UserRejectInviteDto {

  @NotEmpty
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "邀请码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String inviteCode;

}
