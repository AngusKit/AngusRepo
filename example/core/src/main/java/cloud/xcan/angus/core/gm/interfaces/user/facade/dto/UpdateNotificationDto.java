package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新通知偏好设置请求参数")
public class UpdateNotificationDto {

  @Schema(description = "邮件通知设置")
  private EmailNotificationSettingsDto emailNotifications;

  @Schema(description = "推送通知设置")
  private PushNotificationSettingsDto pushNotifications;

  @Schema(description = "桌面通知开关")
  private Boolean desktopNotifications;

  @Schema(description = "通知声音开关")
  private Boolean notificationSound;

  /**
   * 邮件通知设置
   */
  @Getter
  @Setter
  @Accessors(chain = true)
  @Schema(description = "邮件通知设置")
  public static class EmailNotificationSettingsDto {

    @Schema(description = "评论通知")
    private Boolean comments;

    @Schema(description = "@提及通知")
    private Boolean mentions;

    @Schema(description = "更新通知")
    private Boolean updates;

    @Schema(description = "产品新闻")
    private Boolean productNews;
  }

  /**
   * 推送通知设置
   */
  @Getter
  @Setter
  @Accessors(chain = true)
  @Schema(description = "推送通知设置")
  public static class PushNotificationSettingsDto {

    @Schema(description = "评论通知")
    private Boolean comments;

    @Schema(description = "@提及通知")
    private Boolean mentions;

    @Schema(description = "更新通知")
    private Boolean updates;
  }
}
