package cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.user.UserSetting;
import cloud.xcan.angus.api.commonlink.user.model.AppearanceValue;
import cloud.xcan.angus.api.commonlink.user.model.NotificationValue;
import cloud.xcan.angus.api.commonlink.user.model.NotificationValue.EmailNotificationSettings;
import cloud.xcan.angus.api.commonlink.user.model.NotificationValue.PushNotificationSettings;
import cloud.xcan.angus.core.gm.domain.system.Language;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UpdateAppearanceDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UpdateNotificationDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.AppearancePreferencesVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.LanguageVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.NotificationPreferencesVo;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户设置数据组装器
 */
public class UserSettingAssembler {

  public static AppearanceValue toAppearanceValue(UpdateAppearanceDto dto,
      AppearanceValue existing) {
    AppearanceValue value = existing != null ? existing : new AppearanceValue();
    if (dto.getTheme() != null) {
      value.setTheme(dto.getTheme());
    }
    if (dto.getLanguage() != null) {
      value.setLanguage(dto.getLanguage());
    }
    if (dto.getFontSize() != null) {
      value.setFontSize(dto.getFontSize());
    }
    return value;
  }

  public static AppearancePreferencesVo toAppearancePreferencesVo(UserSetting setting) {
    AppearancePreferencesVo vo = new AppearancePreferencesVo();
    if (setting != null) {
      vo.setId(setting.getId());
      vo.setUserId(setting.getUserId());
      if (setting.getValue() instanceof AppearanceValue appearanceValue) {
        vo.setTheme(appearanceValue.getTheme());
        vo.setLanguage(appearanceValue.getLanguage());
        vo.setFontSize(appearanceValue.getFontSize());
      }
    }
    return vo;
  }

  public static NotificationValue toNotificationValue(UpdateNotificationDto dto,
      NotificationValue existing) {
    NotificationValue value = existing != null ? existing : new NotificationValue();
    if (dto.getEmailNotifications() != null) {
      EmailNotificationSettings emailSettings = new EmailNotificationSettings();
      emailSettings.setComments(dto.getEmailNotifications().getComments());
      emailSettings.setMentions(dto.getEmailNotifications().getMentions());
      emailSettings.setUpdates(dto.getEmailNotifications().getUpdates());
      emailSettings.setProductNews(dto.getEmailNotifications().getProductNews());
      value.setEmailNotifications(emailSettings);
    }
    if (dto.getPushNotifications() != null) {
      PushNotificationSettings pushSettings = new PushNotificationSettings();
      pushSettings.setComments(dto.getPushNotifications().getComments());
      pushSettings.setMentions(dto.getPushNotifications().getMentions());
      pushSettings.setUpdates(dto.getPushNotifications().getUpdates());
      value.setPushNotifications(pushSettings);
    }
    if (dto.getDesktopNotifications() != null) {
      value.setDesktopNotifications(dto.getDesktopNotifications());
    }
    if (dto.getNotificationSound() != null) {
      value.setNotificationSound(dto.getNotificationSound());
    }
    return value;
  }

  public static NotificationPreferencesVo toNotificationPreferencesVo(UserSetting setting) {
    NotificationPreferencesVo vo = new NotificationPreferencesVo();
    if (setting != null) {
      vo.setId(setting.getId());
      vo.setUserId(setting.getUserId());
      if (setting.getValue() instanceof NotificationValue notificationValue) {
        if (notificationValue.getEmailNotifications() != null) {
          NotificationPreferencesVo.EmailNotificationSettingsVo emailVo =
              new NotificationPreferencesVo.EmailNotificationSettingsVo();
          emailVo.setComments(notificationValue.getEmailNotifications().getComments());
          emailVo.setMentions(notificationValue.getEmailNotifications().getMentions());
          emailVo.setUpdates(notificationValue.getEmailNotifications().getUpdates());
          emailVo.setProductNews(notificationValue.getEmailNotifications().getProductNews());
          vo.setEmailNotifications(emailVo);
        }
        if (notificationValue.getPushNotifications() != null) {
          NotificationPreferencesVo.PushNotificationSettingsVo pushVo =
              new NotificationPreferencesVo.PushNotificationSettingsVo();
          pushVo.setComments(notificationValue.getPushNotifications().getComments());
          pushVo.setMentions(notificationValue.getPushNotifications().getMentions());
          pushVo.setUpdates(notificationValue.getPushNotifications().getUpdates());
          vo.setPushNotifications(pushVo);
        }
        vo.setDesktopNotifications(notificationValue.getDesktopNotifications());
        vo.setNotificationSound(notificationValue.getNotificationSound());
      }
    }
    return vo;
  }

  public static LanguageVo toLanguageVo(Language language) {
    if (language == null) {
      return null;
    }

    LanguageVo vo = new LanguageVo();
    vo.setCode(language.getCode());
    vo.setName(language.getName());
    vo.setNativeName(language.getNativeName());
    return vo;
  }

  public static List<LanguageVo> toLanguageVoList(List<Language> languages) {
    if (languages == null) {
      return null;
    }
    return languages.stream().map(UserSettingAssembler::toLanguageVo)
        .collect(Collectors.toList());
  }
}
