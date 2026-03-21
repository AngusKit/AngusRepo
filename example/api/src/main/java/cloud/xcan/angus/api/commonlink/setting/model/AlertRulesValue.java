package cloud.xcan.angus.api.commonlink.setting.model;

import cloud.xcan.angus.api.commonlink.setting.alert.AlertRuleSettings;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 告警规则设置值
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "告警规则设置值")
public class AlertRulesValue extends SettingValue {

  @Schema(description = "告警规则设置数据")
  private AlertRuleSettings alertRules;
}
