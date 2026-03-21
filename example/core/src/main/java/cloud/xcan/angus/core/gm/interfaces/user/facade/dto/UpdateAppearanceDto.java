package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import cloud.xcan.angus.api.commonlink.user.model.ThemeMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新外观偏好设置请求参数")
public class UpdateAppearanceDto {

  @NotNull
  @Schema(description = "主题模式", requiredMode = Schema.RequiredMode.REQUIRED)
  private ThemeMode theme;

  @NotBlank
  @Length(max = 10)
  @Schema(description = "语言代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "zh-CN")
  private String language;

  @NotNull
  @Min(12)
  @Max(20)
  @Schema(description = "字体大小（px）", requiredMode = Schema.RequiredMode.REQUIRED, example = "14")
  private Integer fontSize;
}
