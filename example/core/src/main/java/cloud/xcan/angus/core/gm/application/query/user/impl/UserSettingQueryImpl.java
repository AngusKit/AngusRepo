package cloud.xcan.angus.core.gm.application.query.user.impl;

import cloud.xcan.angus.api.commonlink.user.UserSetting;
import cloud.xcan.angus.api.commonlink.user.UserSettingRepo;
import cloud.xcan.angus.api.commonlink.user.enums.UserSettingKey;
import cloud.xcan.angus.api.commonlink.user.model.AppearanceValue;
import cloud.xcan.angus.api.commonlink.user.model.NotificationValue;
import cloud.xcan.angus.api.commonlink.user.model.ThemeMode;
import cloud.xcan.angus.core.gm.application.query.user.UserSettingQuery;
import cloud.xcan.angus.core.gm.domain.user.enums.OAuthProvider;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserSettingQueryImpl implements UserSettingQuery {

  @Resource
  private UserSettingRepo userSettingRepo;

  @Override
  public UserSetting findByUserIdAndKey(Long userId, UserSettingKey key) {
    return userSettingRepo.findByUserIdAndKey(userId, key)
        .orElseGet(() -> {
          UserSetting setting = new UserSetting();
          setting.setUserId(userId);
          setting.setKey(key);
          // 设置默认值
          switch (key) {
            case APPEARANCE -> {
              AppearanceValue defaultValue = new AppearanceValue();
              defaultValue.setTheme(ThemeMode.LIGHT);
              defaultValue.setLanguage("zh-CN");
              defaultValue.setFontSize(14);
              setting.setValue(defaultValue);
            }
            case NOTIFICATION -> {
              NotificationValue defaultValue = new NotificationValue();
              defaultValue.setDesktopNotifications(true);
              defaultValue.setNotificationSound(true);
              setting.setValue(defaultValue);
            }
            case SECURITY -> {
              // SecurityValue 默认值在 SecurityValue 类中定义
              setting.setValue(new cloud.xcan.angus.api.commonlink.user.model.SecurityValue());
            }
          }
          return setting;
        });
  }


  /**
   * 根据第三方用户ID查找本地用户
   *
   * <p>通过查询SettingUser表中的独立字段来匹配第三方用户ID。
   * 使用数据库索引查询，性能优于之前的findAll()遍历方式。
   */
  @Override
  public UserSetting findUserByOAuthId(OAuthProvider provider, String openId) {
    if (openId == null) {
      return null;
    }

    // 根据provider类型使用对应的查询方法，利用数据库索引提高查询性能
    Optional<UserSetting> settingUserOpt = switch (provider) {
      case WECHAT -> userSettingRepo.findByWechatUserId(openId);
      case GITHUB -> userSettingRepo.findByGithubUserId(openId);
      case GOOGLE -> userSettingRepo.findByGoogleUserId(openId);
    };
    return settingUserOpt.orElse(null);
  }
}
