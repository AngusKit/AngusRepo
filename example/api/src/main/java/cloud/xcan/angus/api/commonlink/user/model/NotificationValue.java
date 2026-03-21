package cloud.xcan.angus.api.commonlink.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 通知偏好设置值
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通知偏好设置值")
public class NotificationValue extends UserSettingValue {

  @Schema(description = "邮件通知设置")
  private EmailNotificationSettings emailNotifications;

  @Schema(description = "推送通知设置")
  private PushNotificationSettings pushNotifications;

  @Schema(description = "桌面通知开关")
  private Boolean desktopNotifications;

  @Schema(description = "通知声音开关")
  private Boolean notificationSound;

  /**
   * 邮件通知设置
   */
  @Setter
  @Getter
  public static class EmailNotificationSettings {

    private Boolean comments;         // 评论通知
    private Boolean mentions;         // @提及通知
    private Boolean updates;          // 更新通知
    private Boolean productNews;      // 产品新闻
  }

  /**
   * 推送通知设置
   */
  @Setter
  @Getter
  public static class PushNotificationSettings {

    private Boolean comments;         // 评论通知
    private Boolean mentions;         // @提及通知
    private Boolean updates;          // 更新通知
  }
}
