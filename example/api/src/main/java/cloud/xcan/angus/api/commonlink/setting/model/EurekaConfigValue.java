package cloud.xcan.angus.api.commonlink.setting.model;

import cloud.xcan.angus.api.commonlink.setting.eureka.EurekaConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Eureka配置设置值
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Eureka配置设置值")
public class EurekaConfigValue extends SettingValue {

  @Schema(description = "Eureka配置数据")
  private EurekaConfig eurekaConfig;
}
