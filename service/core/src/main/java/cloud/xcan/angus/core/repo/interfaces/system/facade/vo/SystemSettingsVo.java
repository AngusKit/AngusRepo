package cloud.xcan.angus.core.repo.interfaces.system.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "系统设置信息")
public class SystemSettingsVo implements Serializable {

  @Schema(description = "通用设置")
  private GeneralSettingsVo general;

  @Schema(description = "存储设置")
  private StorageSettingsVo storage;

  @Schema(description = "认证设置")
  private AuthSettingsVo authentication;

  @Schema(description = "集成设置")
  private IntegrationSettingsVo integrations;
}
