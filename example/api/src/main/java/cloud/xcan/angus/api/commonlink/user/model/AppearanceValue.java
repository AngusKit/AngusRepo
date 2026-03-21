package cloud.xcan.angus.api.commonlink.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 外观偏好设置值
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "外观偏好设置值")
public class AppearanceValue extends UserSettingValue {

  @Schema(description = "主题模式")
  private ThemeMode theme;

  @Schema(description = "语言代码")
  private String language;

  @Schema(description = "字体大小（px）")
  private Integer fontSize;
}
