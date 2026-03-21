package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import cloud.xcan.angus.api.commonlink.user.model.ThemeMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户外观偏好设置详情")
public class AppearancePreferencesVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "用户ID")
  private Long userId;

  @Schema(description = "主题模式")
  private ThemeMode theme;

  @Schema(description = "语言代码")
  private String language;

  @Schema(description = "字体大小（px）")
  private Integer fontSize;
}
