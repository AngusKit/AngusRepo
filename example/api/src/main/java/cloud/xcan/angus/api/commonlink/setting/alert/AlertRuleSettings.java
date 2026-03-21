package cloud.xcan.angus.api.commonlink.setting.alert;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AlertRuleSettings {

  /**
   * 告警规则列表
   */
  private List<AlertRule> rules = new ArrayList<>();
}
