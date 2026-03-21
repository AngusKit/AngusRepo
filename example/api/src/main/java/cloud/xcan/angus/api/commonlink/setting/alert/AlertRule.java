package cloud.xcan.angus.api.commonlink.setting.alert;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AlertRule {

  /**
   * 规则名称
   */
  private String name;

  /**
   * 监控指标
   */
  private String metric;

  /**
   * 条件（>, <, >=, <=, ==）
   */
  private String condition;

  /**
   * 阈值
   */
  private Double threshold;

  /**
   * 持续时间（秒）
   */
  private Integer duration;

  /**
   * 告警等级
   */
  private AlertLevel level;
}
