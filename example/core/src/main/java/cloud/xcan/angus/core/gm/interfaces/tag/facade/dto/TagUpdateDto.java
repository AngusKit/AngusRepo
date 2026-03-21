package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "更新标签DTO")
public class TagUpdateDto {

  @NotBlank
  @Pattern(regexp = "^[A-Z][A-Z0-9_]*$")
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "标签名称，大写字母+下划线", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "标签描述", requiredMode = RequiredMode.REQUIRED)
  private String description;

  @NotNull
  @Schema(description = "所属分类ID", requiredMode = RequiredMode.REQUIRED)
  private Long categoryId;
}
