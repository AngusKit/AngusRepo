package cloud.xcan.angus.core.repo.interfaces.user.facade.internal.assembler;

import cloud.xcan.angus.core.repo.domain.user.UserApiToken;
import cloud.xcan.angus.core.repo.domain.user.UserProfile;
import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.ApiTokenCreateDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.UserProfileUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.vo.ApiTokenVo;
import cloud.xcan.angus.core.repo.interfaces.user.facade.vo.UserProfileVo;

public class UserProfileAssembler {

  public static UserProfile toUpdateEntity(UserProfileUpdateDto dto, Long userId) {
    UserProfile entity = new UserProfile();
    entity.setId(userId);
    entity.setName(dto.getName());
    entity.setAvatar(dto.getAvatar());
    entity.setDepartment(dto.getDepartment());
    return entity;
  }

  public static UserProfileVo toProfileVo(UserProfile profile) {
    if (profile == null) {
      return null;
    }
    UserProfileVo vo = new UserProfileVo();
    vo.setId(profile.getId());
    if (profile.getTenantId() != null) {
      vo.setTenantId(profile.getTenantId());
    }
    vo.setName(profile.getName());
    vo.setEmail(profile.getEmail());
    vo.setAvatar(profile.getAvatar());
    vo.setRole(profile.getRole());
    vo.setDepartment(profile.getDepartment());
    vo.setJoinedDate(profile.getJoinedDate());
    vo.setLastLogin(profile.getLastLogin());
    vo.setPreferences(profile.getPreferences());
    vo.setNotificationSettings(profile.getNotificationSettings());
    return vo;
  }

  public static UserApiToken toTokenEntity(ApiTokenCreateDto dto, Long userId, String tokenHash) {
    UserApiToken token = new UserApiToken();
    token.setUserId(userId);
    token.setName(dto.getName());
    token.setDescription(dto.getDescription());
    token.setPermission(dto.getPermission());
    token.setTokenHash(tokenHash);
    token.setExpiresAt(dto.getExpiresAt());
    return token;
  }

  public static ApiTokenVo toTokenVo(UserApiToken token) {
    if (token == null) {
      return null;
    }
    ApiTokenVo vo = new ApiTokenVo();
    vo.setId(token.getId());
    vo.setName(token.getName());
    vo.setDescription(token.getDescription());
    vo.setPermission(token.getPermission());
    vo.setEnabled(token.getEnabled());
    vo.setExpiresAt(token.getExpiresAt());
    vo.setLastUsed(token.getLastUsed());
    vo.setUsageCount(token.getUsageCount());
    vo.setCreatedDate(token.getCreatedDate());
    return vo;
  }
}
