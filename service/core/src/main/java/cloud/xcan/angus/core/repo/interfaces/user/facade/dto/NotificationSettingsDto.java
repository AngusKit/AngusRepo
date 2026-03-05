package cloud.xcan.angus.core.repo.interfaces.user.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "通知设置请求参数")
public class NotificationSettingsDto {

  @Schema(description = "是否启用邮件通知")
  private Boolean emailEnabled;

  @Schema(description = "安全告警通知")
  private Boolean securityAlerts;

  @Schema(description = "制品上传通知")
  private Boolean uploadNotifications;

  @Schema(description = "制品下载通知")
  private Boolean downloadNotifications;

  @Schema(description = "系统通知")
  private Boolean systemNotifications;
}
