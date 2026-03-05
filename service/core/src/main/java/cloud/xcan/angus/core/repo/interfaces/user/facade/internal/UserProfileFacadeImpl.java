package cloud.xcan.angus.core.repo.interfaces.user.facade.internal;

import static cloud.xcan.angus.core.repo.interfaces.user.facade.internal.assembler.UserProfileAssembler.toProfileVo;
import static cloud.xcan.angus.core.repo.interfaces.user.facade.internal.assembler.UserProfileAssembler.toTokenEntity;
import static cloud.xcan.angus.core.repo.interfaces.user.facade.internal.assembler.UserProfileAssembler.toTokenVo;
import static cloud.xcan.angus.core.repo.interfaces.user.facade.internal.assembler.UserProfileAssembler.toUpdateEntity;

import cloud.xcan.angus.core.repo.application.cmd.user.UserProfileCmd;
import cloud.xcan.angus.core.repo.application.query.user.UserProfileQuery;
import cloud.xcan.angus.core.repo.domain.user.UserApiToken;
import cloud.xcan.angus.core.repo.domain.user.UserProfile;
import cloud.xcan.angus.core.repo.interfaces.user.facade.UserProfileFacade;
import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.ApiTokenCreateDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.NotificationSettingsDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.PasswordChangeDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.UserPreferencesUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.UserProfileUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.vo.ApiTokenVo;
import cloud.xcan.angus.core.repo.interfaces.user.facade.vo.AvatarUploadResultVo;
import cloud.xcan.angus.core.repo.interfaces.user.facade.vo.PasswordChangeResultVo;
import cloud.xcan.angus.core.repo.interfaces.user.facade.vo.UserProfileVo;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserProfileFacadeImpl implements UserProfileFacade {

  @Resource
  private UserProfileCmd userProfileCmd;

  @Resource
  private UserProfileQuery userProfileQuery;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public UserProfileVo getProfile() {
    Long userId = PrincipalContext.getUserId();
    UserProfile profile = userProfileQuery.findAndCheck(userId);
    return toProfileVo(profile);
  }

  @Override
  public UserProfileVo updateProfile(UserProfileUpdateDto dto) {
    Long userId = PrincipalContext.getUserId();
    UserProfile entity = toUpdateEntity(dto, userId);
    UserProfile updated = userProfileCmd.updateProfile(entity);
    return toProfileVo(updated);
  }

  @Override
  public PasswordChangeResultVo changePassword(PasswordChangeDto dto) {
    Long userId = PrincipalContext.getUserId();
    PasswordChangeResultVo result = new PasswordChangeResultVo();
    try {
      if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
        result.setSuccess(false);
        result.setMessage("新密码和确认密码不一致");
        return result;
      }
      userProfileCmd.changePassword(userId, dto.getCurrentPassword(), dto.getNewPassword());
      result.setSuccess(true);
      result.setMessage("密码修改成功");
    } catch (Exception e) {
      result.setSuccess(false);
      result.setMessage("密码修改失败: " + e.getMessage());
    }
    return result;
  }

  @Override
  public UserProfileVo updatePreferences(UserPreferencesUpdateDto dto) {
    Long userId = PrincipalContext.getUserId();
    try {
      String preferencesJson = objectMapper.writeValueAsString(dto);
      UserProfile updated = userProfileCmd.updatePreferences(userId, preferencesJson);
      return toProfileVo(updated);
    } catch (Exception e) {
      throw new RuntimeException("更新偏好设置失败", e);
    }
  }

  @Override
  public UserProfileVo getNotificationSettings() {
    Long userId = PrincipalContext.getUserId();
    UserProfile profile = userProfileQuery.findAndCheck(userId);
    return toProfileVo(profile);
  }

  @Override
  public UserProfileVo updateNotificationSettings(NotificationSettingsDto dto) {
    Long userId = PrincipalContext.getUserId();
    try {
      String settingsJson = objectMapper.writeValueAsString(dto);
      UserProfile updated = userProfileCmd.updateNotificationSettings(userId, settingsJson);
      return toProfileVo(updated);
    } catch (Exception e) {
      throw new RuntimeException("更新通知设置失败", e);
    }
  }

  @Override
  public ApiTokenVo createApiToken(ApiTokenCreateDto dto) {
    Long userId = PrincipalContext.getUserId();
    String rawToken = UUID.randomUUID().toString().replace("-", "");
    String tokenHash = rawToken;
    UserApiToken token = toTokenEntity(dto, userId, tokenHash);
    UserApiToken created = userProfileCmd.createApiToken(token);
    ApiTokenVo vo = toTokenVo(created);
    vo.setToken(rawToken);
    return vo;
  }

  @Override
  public List<ApiTokenVo> listApiTokens() {
    Long userId = PrincipalContext.getUserId();
    List<UserApiToken> tokens = userProfileQuery.findTokensByUserId(userId);
    return tokens.stream().map(t -> toTokenVo(t)).collect(Collectors.toList());
  }

  @Override
  public void revokeApiToken(Long tokenId) {
    userProfileCmd.revokeApiToken(tokenId);
  }

  @Override
  public AvatarUploadResultVo uploadAvatar(String fileName, byte[] content) {
    Long userId = PrincipalContext.getUserId();
    String avatarUrl = "/avatars/" + userId + "/" + fileName;
    userProfileCmd.updateAvatar(userId, avatarUrl);

    AvatarUploadResultVo result = new AvatarUploadResultVo();
    result.setAvatarUrl(avatarUrl);
    result.setSuccess(true);
    return result;
  }
}
