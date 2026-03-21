package cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Data
@Accessors(chain = true)
@Schema(description = "验证邀请码请求参数")
public class InviteCodeVerifyDto {

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "邀请码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String inviteCode;
}
