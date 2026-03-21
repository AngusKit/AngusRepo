package cloud.xcan.angus.api.gm.user.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "创建用户令牌请求参数")
public class TokenCreateDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "令牌名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "API访问令牌")
  private String name;

  @NotBlank
  @Schema(description = "当前用户密码（用于验证身份）", requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "令牌描述", example = "用于第三方系统集成")
  private String description;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "授权应用编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "AngusGM")
  private String appCode;

  @NotEmpty
  @Schema(description = "权限范围列表", requiredMode = Schema.RequiredMode.REQUIRED,
      example = "[\"management\", \"read\", \"write\"]")
  private List<String> scopes;

  @NotNull
  @Min(1)
  @Max(365)
  @Schema(description = "有效期（天数）", requiredMode = Schema.RequiredMode.REQUIRED, example = "365")
  private Integer expiresInDays;
}
