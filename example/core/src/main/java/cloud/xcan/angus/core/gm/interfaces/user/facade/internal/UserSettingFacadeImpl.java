package cloud.xcan.angus.core.gm.interfaces.user.facade.internal;

import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;

import cloud.xcan.angus.api.commonlink.user.UserSetting;
import cloud.xcan.angus.api.commonlink.user.enums.UserSettingKey;
import cloud.xcan.angus.api.commonlink.user.model.AppearanceValue;
import cloud.xcan.angus.api.commonlink.user.model.NotificationValue;
import cloud.xcan.angus.api.commonlink.user.model.NotificationValue.EmailNotificationSettings;
import cloud.xcan.angus.api.commonlink.user.model.NotificationValue.PushNotificationSettings;
import cloud.xcan.angus.core.gm.application.cmd.user.UserSettingCmd;
import cloud.xcan.angus.core.gm.application.query.user.LanguageQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserSettingQuery;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserSettingFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.BatchUpdateNotificationDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UpdateAppearanceDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UpdateNotificationDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserSettingAssembler;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.AppearancePreferencesVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.LanguageVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.NotificationPreferencesVo;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UserSettingFacadeImpl implements UserSettingFacade {

  @Resource
  private UserSettingCmd userSettingCmd;

  @Resource
  private UserSettingQuery userSettingQuery;

  @Resource
  private LanguageQuery languageQuery;

  @Override
  public AppearancePreferencesVo updateAppearance(UpdateAppearanceDto dto) {
    Long userId = getUserId();
    UserSetting existing = userSettingQuery.findByUserIdAndKey(userId, UserSettingKey.APPEARANCE);
    AppearanceValue existingValue =
        existing != null && existing.getValue() instanceof AppearanceValue
            ? (AppearanceValue) existing.getValue() : null;
    AppearanceValue value = UserSettingAssembler.toAppearanceValue(dto, existingValue);
    UserSetting saved = userSettingCmd.update(userId, UserSettingKey.APPEARANCE, value);
    return UserSettingAssembler.toAppearancePreferencesVo(saved);
  }

  @Override
  public AppearancePreferencesVo getAppearance() {
    Long userId = getUserId();
    UserSetting setting = userSettingQuery.findByUserIdAndKey(userId, UserSettingKey.APPEARANCE);
    return UserSettingAssembler.toAppearancePreferencesVo(setting);
  }

  @Override
  public List<LanguageVo> getSupportedLanguages() {
    return UserSettingAssembler.toLanguageVoList(languageQuery.findEnabledLanguages());
  }

  @Override
  public NotificationPreferencesVo updateNotification(UpdateNotificationDto dto) {
    Long userId = getUserId();
    UserSetting existing = userSettingQuery.findByUserIdAndKey(userId, UserSettingKey.NOTIFICATION);
    NotificationValue existingValue = existing != null
        && existing.getValue() instanceof NotificationValue
        ? (NotificationValue) existing.getValue()
        : null;
    NotificationValue value = UserSettingAssembler.toNotificationValue(dto, existingValue);
    UserSetting saved = userSettingCmd.update(userId, UserSettingKey.NOTIFICATION, value);
    return UserSettingAssembler.toNotificationPreferencesVo(saved);
  }

  @Override
  public NotificationPreferencesVo getNotification() {
    Long userId = getUserId();
    UserSetting setting = userSettingQuery.findByUserIdAndKey(userId, UserSettingKey.NOTIFICATION);
    return UserSettingAssembler.toNotificationPreferencesVo(setting);
  }

  @Override
  public NotificationPreferencesVo batchUpdateNotification(BatchUpdateNotificationDto dto) {
    Long userId = getUserId();
    UserSetting existing = userSettingQuery.findByUserIdAndKey(userId, UserSettingKey.NOTIFICATION);
    NotificationValue existingValue = existing != null
        && existing.getValue() instanceof NotificationValue
        ? (NotificationValue) existing.getValue()
        : new NotificationValue();

    // 批量更新指定渠道的所有通知类型
    if (dto.getChannel() != null) {
      Boolean enabled = dto.getEnabled();
      switch (dto.getChannel()) {
        case EMAIL -> {
          if (existingValue.getEmailNotifications() == null) {
            existingValue.setEmailNotifications(new EmailNotificationSettings());
          }
          existingValue.getEmailNotifications().setComments(enabled);
          existingValue.getEmailNotifications().setMentions(enabled);
          existingValue.getEmailNotifications().setUpdates(enabled);
          existingValue.getEmailNotifications().setProductNews(enabled);
        }
        case PUSH -> {
          if (existingValue.getPushNotifications() == null) {
            existingValue.setPushNotifications(new PushNotificationSettings());
          }
          existingValue.getPushNotifications().setComments(enabled);
          existingValue.getPushNotifications().setMentions(enabled);
          existingValue.getPushNotifications().setUpdates(enabled);
        }
      }
    }

    UserSetting saved = userSettingCmd.update(userId, UserSettingKey.NOTIFICATION, existingValue);
    return UserSettingAssembler.toNotificationPreferencesVo(saved);
  }

}
