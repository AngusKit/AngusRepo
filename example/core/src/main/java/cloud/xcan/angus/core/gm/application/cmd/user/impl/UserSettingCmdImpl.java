package cloud.xcan.angus.core.gm.application.cmd.user.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;

import cloud.xcan.angus.api.commonlink.user.UserSetting;
import cloud.xcan.angus.api.commonlink.user.UserSettingRepo;
import cloud.xcan.angus.api.commonlink.user.enums.UserSettingKey;
import cloud.xcan.angus.api.commonlink.user.model.UserSettingValue;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserSettingCmd;
import cloud.xcan.angus.core.gm.domain.user.enums.OAuthProvider;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSettingCmdImpl extends CommCmd<UserSetting, Long> implements UserSettingCmd {

  @Resource
  private UserSettingRepo userSettingRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserSetting update(Long userId, UserSettingKey key, UserSettingValue value) {
    return new BizTemplate<UserSetting>() {
      @Override
      protected UserSetting process() {
        UserSetting setting = userSettingRepo.findByUserIdAndKey(userId, key).orElse(null);
        if (setting == null) {
          setting = new UserSetting();
          setting.setId(uidGenerator.getUID());
          setting.setUserId(userId);
          setting.setKey(key);
        }
        setting.setValue(value);
        return userSettingRepo.save(setting);
      }
    }.execute();
  }

  /**
   * 更新或创建第三方账号绑定关系
   */
  @Override
  public void updateSocialBinding(Long userId, OAuthProvider provider, String openId) {
    if (userId == null || provider == null || openId == null) {
      return;
    }

    // 获取或创建SettingUser
    var settingUserOpt = userSettingRepo.findById(userId);
    UserSetting settingUser = settingUserOpt.orElseGet(() -> {
      UserSetting newSettingUser = new UserSetting();
      newSettingUser.setId(uidGenerator.getUID());
      newSettingUser.setUserId(userId);
      newSettingUser.setTenantId(getOptTenantId());
      return newSettingUser;
    });

    // 根据provider类型设置对应的绑定信息
    LocalDateTime now = LocalDateTime.now();
    switch (provider) {
      case WECHAT -> {
        settingUser.setWechatUserId(openId);
        settingUser.setWechatUserBindDate(now);
      }
      case GITHUB -> {
        settingUser.setGithubUserId(openId);
        settingUser.setGithubUserBindDate(now);
      }
      case GOOGLE -> {
        settingUser.setGoogleUserId(openId);
        settingUser.setGoogleUserBindDate(now);
      }
    }
    userSettingRepo.save(settingUser);
  }

  @Override
  protected BaseRepository<UserSetting, Long> getRepository() {
    return userSettingRepo;
  }
}
