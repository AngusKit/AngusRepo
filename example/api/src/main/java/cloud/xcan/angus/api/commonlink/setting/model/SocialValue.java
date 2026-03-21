package cloud.xcan.angus.api.commonlink.setting.model;

import cloud.xcan.angus.api.commonlink.setting.social.Social;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "社交设置值")
public class SocialValue extends SettingValue {

  @Schema(description = "社交数据")
  private Social social;
}
