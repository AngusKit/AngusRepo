package cloud.xcan.angus.core.gm.application.cmd.user;

import cloud.xcan.angus.api.commonlink.user.User;

/**
 * 用户个人信息命令服务接口 负责用户个人信息的写操作
 */
public interface UserProfileCmd {

  /**
   * 更新个人信息
   */
  User updateProfile(User user);

  /**
   * 更新头像
   */
  User updateAvatar(Long userId, String avatarUrl);

  /**
   * 删除头像
   */
  User deleteAvatar(Long userId);
}
