package cloud.xcan.angus.core.repo.interfaces.user.facade;

import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.ApiTokenCreateDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.NotificationSettingsDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.PasswordChangeDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.UserPreferencesUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.UserProfileUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.vo.ApiTokenVo;
import cloud.xcan.angus.core.repo.interfaces.user.facade.vo.AvatarUploadResultVo;
import cloud.xcan.angus.core.repo.interfaces.user.facade.vo.PasswordChangeResultVo;
import cloud.xcan.angus.core.repo.interfaces.user.facade.vo.UserProfileVo;
import java.util.List;

public interface UserProfileFacade {

  UserProfileVo getProfile();

  UserProfileVo updateProfile(UserProfileUpdateDto dto);

  PasswordChangeResultVo changePassword(PasswordChangeDto dto);

  UserProfileVo updatePreferences(UserPreferencesUpdateDto dto);

  UserProfileVo getNotificationSettings();

  UserProfileVo updateNotificationSettings(NotificationSettingsDto dto);

  ApiTokenVo createApiToken(ApiTokenCreateDto dto);

  List<ApiTokenVo> listApiTokens();

  void revokeApiToken(Long tokenId);

  AvatarUploadResultVo uploadAvatar(String fileName, byte[] content);
}
