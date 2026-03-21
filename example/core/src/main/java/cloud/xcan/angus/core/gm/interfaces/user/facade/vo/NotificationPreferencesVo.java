package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户通知偏好设置详情")
public class NotificationPreferencesVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "用户ID")
  private Long userId;

  @Schema(description = "邮件通知设置")
  private EmailNotificationSettingsVo emailNotifications;

  @Schema(description = "推送通知设置")
  private PushNotificationSettingsVo pushNotifications;

  @Schema(description = "桌面通知开关")
  private Boolean desktopNotifications;

  @Schema(description = "通知声音开关")
  private Boolean notificationSound;

  /**
   * <p>邮件通知设置</p>
   */
  @Data
  @Schema(description = "邮件通知设置")
  public static class EmailNotificationSettingsVo {

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
   * <p>推送通知设置</p>
   */
  @Data
  @Schema(description = "推送通知设置")
  public static class PushNotificationSettingsVo {

    @Schema(description = "评论通知")
    private Boolean comments;

    @Schema(description = "@提及通知")
    private Boolean mentions;

    @Schema(description = "更新通知")
    private Boolean updates;
  }
}
