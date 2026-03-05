package cloud.xcan.angus.core.repo.application.cmd.user;

import cloud.xcan.angus.core.repo.domain.user.UserApiToken;
import cloud.xcan.angus.core.repo.domain.user.UserProfile;

public interface UserProfileCmd {

  UserProfile updateProfile(UserProfile profile);

  void changePassword(Long userId, String currentPassword, String newPassword);

  UserProfile updatePreferences(Long userId, String preferences);

  UserProfile updateNotificationSettings(Long userId, String notificationSettings);

  UserApiToken createApiToken(UserApiToken token);

  void revokeApiToken(Long tokenId);

  UserProfile updateAvatar(Long userId, String avatarUrl);
}
