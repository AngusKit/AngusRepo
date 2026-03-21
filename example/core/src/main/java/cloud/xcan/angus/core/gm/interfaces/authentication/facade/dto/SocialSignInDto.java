package cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;

import cloud.xcan.angus.core.gm.domain.user.enums.OAuthProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Data
@Accessors(chain = true)
@Schema(description = "第三方登录请求参数")
public class SocialSignInDto {

  @NotNull
  @Schema(description = "第三方登录提供商", requiredMode = Schema.RequiredMode.REQUIRED)
  private OAuthProvider provider;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "OAuth授权码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String code;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "OAuth状态参数")
  private String state;
}
