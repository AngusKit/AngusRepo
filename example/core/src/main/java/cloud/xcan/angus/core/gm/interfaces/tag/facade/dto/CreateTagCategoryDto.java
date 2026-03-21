package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "创建标签分类DTO")
public class CreateTagCategoryDto implements Serializable {

  @NotBlank
  @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_]*$")
  @Schema(description = "分类编码，字母+数字+下划线", requiredMode = RequiredMode.REQUIRED)
  private String code;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "分类名称", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "分类描述")
  private String description;
}
