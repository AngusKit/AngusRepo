package cloud.xcan.angus.core.gm.application.query.user;

import cloud.xcan.angus.api.commonlink.user.UserSetting;
import cloud.xcan.angus.api.commonlink.user.enums.UserSettingKey;
import cloud.xcan.angus.core.gm.domain.user.enums.OAuthProvider;

public interface UserSettingQuery {

  UserSetting findByUserIdAndKey(Long userId, UserSettingKey key);

  UserSetting findUserByOAuthId(OAuthProvider provider, String openId);
}
