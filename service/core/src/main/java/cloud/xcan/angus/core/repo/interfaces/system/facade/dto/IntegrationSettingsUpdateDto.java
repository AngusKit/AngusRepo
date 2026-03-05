package cloud.xcan.angus.core.repo.interfaces.system.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "集成设置更新请求参数")
public class IntegrationSettingsUpdateDto {

  @Schema(description = "SMTP配置（JSON）")
  private String smtpConfig;

  @Schema(description = "Slack集成配置（JSON）")
  private String slackConfig;

  @Schema(description = "Webhook全局配置（JSON）")
  private String webhookConfig;
}
