package cloud.xcan.angus.core.gm.interfaces.system.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "告警规则设置详情")
public class AlertRuleSettingsVo {

  @Schema(description = "告警规则列表")
  private List<AlertRuleVo> rules;
}
