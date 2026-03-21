package cloud.xcan.angus.core.gm.application.cmd.user;

import cloud.xcan.angus.api.commonlink.user.UserSetting;
import cloud.xcan.angus.api.commonlink.user.enums.UserSettingKey;
import cloud.xcan.angus.api.commonlink.user.model.UserSettingValue;
import cloud.xcan.angus.core.gm.domain.user.enums.OAuthProvider;

public interface UserSettingCmd {

  UserSetting update(Long userId, UserSettingKey key, UserSettingValue value);

  void updateSocialBinding(Long userId, OAuthProvider provider, String openId);

}
