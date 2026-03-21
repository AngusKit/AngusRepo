package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "更新标签分类DTO")
public class UpdateTagCategoryDto implements Serializable {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "分类名称", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "分类描述")
  private String description;
}
