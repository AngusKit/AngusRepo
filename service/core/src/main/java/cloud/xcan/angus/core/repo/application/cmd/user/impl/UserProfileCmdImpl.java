package cloud.xcan.angus.core.repo.application.cmd.user.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.user.UserProfileCmd;
import cloud.xcan.angus.core.repo.domain.user.UserApiToken;
import cloud.xcan.angus.core.repo.domain.user.UserApiTokenRepo;
import cloud.xcan.angus.core.repo.domain.user.UserProfile;
import cloud.xcan.angus.core.repo.domain.user.UserProfileRepo;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class UserProfileCmdImpl extends CommCmd<UserProfile, Long> implements UserProfileCmd {

  @Autowired(required = false)
  private UserProfileRepo userProfileRepo;

  @Autowired(required = false)
  private UserApiTokenRepo userApiTokenRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserProfile updateProfile(UserProfile profile) {
    return new BizTemplate<UserProfile>() {
      UserProfile existing;

      @Override
      protected void checkParams() {
        existing = userProfileRepo.findById(profile.getId())
            .orElseThrow(() -> new RuntimeException("用户不存在: " + profile.getId()));
      }

      @Override
      protected UserProfile process() {
        existing.setName(profile.getName());
        existing.setDepartment(profile.getDepartment());
        existing.setModifiedDate(LocalDateTime.now());
        userProfileRepo.save(existing);
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void changePassword(Long userId, String currentPassword, String newPassword) {
    new BizTemplate<Void>() {
      UserProfile existing;

      @Override
      protected void checkParams() {
        existing = userProfileRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
      }

      @Override
      protected Void process() {
        existing.setPasswordHash(newPassword);
        existing.setModifiedDate(LocalDateTime.now());
        userProfileRepo.save(existing);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserProfile updatePreferences(Long userId, String preferences) {
    return new BizTemplate<UserProfile>() {
      UserProfile existing;

      @Override
      protected void checkParams() {
        existing = userProfileRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
      }

      @Override
      protected UserProfile process() {
        existing.setPreferences(preferences);
        existing.setModifiedDate(LocalDateTime.now());
        userProfileRepo.save(existing);
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserProfile updateNotificationSettings(Long userId, String notificationSettings) {
    return new BizTemplate<UserProfile>() {
      UserProfile existing;

      @Override
      protected void checkParams() {
        existing = userProfileRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
      }

      @Override
      protected UserProfile process() {
        existing.setNotificationSettings(notificationSettings);
        existing.setModifiedDate(LocalDateTime.now());
        userProfileRepo.save(existing);
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserApiToken createApiToken(UserApiToken token) {
    return new BizTemplate<UserApiToken>() {
      @Override
      protected UserApiToken process() {
        token.setCreatedDate(LocalDateTime.now());
        if (token.getEnabled() == null) {
          token.setEnabled(true);
        }
        if (token.getUsageCount() == null) {
          token.setUsageCount(0L);
        }
        userApiTokenRepo.save(token);
        return token;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void revokeApiToken(Long tokenId) {
    userApiTokenRepo.deleteById(tokenId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserProfile updateAvatar(Long userId, String avatarUrl) {
    return new BizTemplate<UserProfile>() {
      UserProfile existing;

      @Override
      protected void checkParams() {
        existing = userProfileRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
      }

      @Override
      protected UserProfile process() {
        existing.setAvatar(avatarUrl);
        existing.setModifiedDate(LocalDateTime.now());
        userProfileRepo.save(existing);
        return existing;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<UserProfile, Long> getRepository() {
    return this.userProfileRepo;
  }
}
