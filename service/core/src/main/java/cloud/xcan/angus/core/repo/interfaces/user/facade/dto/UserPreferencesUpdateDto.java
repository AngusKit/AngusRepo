package cloud.xcan.angus.core.repo.interfaces.user.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新偏好设置请求参数")
public class UserPreferencesUpdateDto implements Serializable {

  @Schema(description = "语言")
  private String language;

  @Schema(description = "主题")
  private String theme;

  @Schema(description = "时区")
  private String timezone;

  @Schema(description = "日期格式")
  private String dateFormat;
}
