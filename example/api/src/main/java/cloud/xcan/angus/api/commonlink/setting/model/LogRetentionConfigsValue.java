package cloud.xcan.angus.api.commonlink.setting.model;

import cloud.xcan.angus.api.commonlink.setting.logretention.LogRetentionConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 日志保留配置设置值
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "日志保留配置设置值")
public class LogRetentionConfigsValue extends SettingValue {

  @Schema(description = "日志保留配置列表")
  private List<LogRetentionConfig> logRetentionConfigs;
}
