package cloud.xcan.angus.api.commonlink.user;

import cloud.xcan.angus.api.commonlink.user.enums.UserSettingKey;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * 用户设置仓储接口
 */
@DoInFuture("Add cache")
@Repository("commonUserSettingRepo")
public interface UserSettingRepo extends BaseRepository<UserSetting, Long> {

  /**
   * 根据用户ID和设置键查找用户设置
   */
  Optional<UserSetting> findByUserIdAndKey(Long userId, UserSettingKey key);

  /**
   * 根据微信用户ID查找用户设置
   */
  Optional<UserSetting> findByWechatUserId(String wechatUserId);

  /**
   * 根据GitHub用户ID查找用户设置
   */
  Optional<UserSetting> findByGithubUserId(String githubUserId);

  /**
   * 根据Google用户ID查找用户设置
   */
  Optional<UserSetting> findByGoogleUserId(String googleUserId);
}

