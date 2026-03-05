package cloud.xcan.angus.core.repo.interfaces.system.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "集成设置信息")
public class IntegrationSettingsVo {

  @Schema(description = "SMTP配置")
  private String smtpConfig;

  @Schema(description = "Slack集成配置")
  private String slackConfig;

  @Schema(description = "Webhook全局配置")
  private String webhookConfig;
}
