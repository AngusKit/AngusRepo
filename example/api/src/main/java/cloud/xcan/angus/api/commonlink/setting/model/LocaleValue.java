package cloud.xcan.angus.api.commonlink.setting.model;

import cloud.xcan.angus.api.commonlink.setting.locale.Locale;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 语言环境设置值
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "语言环境设置值")
public class LocaleValue extends SettingValue {

  @Schema(description = "语言环境数据")
  private Locale locale;
}
