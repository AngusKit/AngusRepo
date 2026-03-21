package cloud.xcan.angus.api.gm.user.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新令牌信息请求参数")
public class TokenUpdateDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "令牌名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "API访问令牌（生产环境）")
  private String name;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "令牌描述", example = "用于生产环境第三方系统集成")
  private String description;
}
