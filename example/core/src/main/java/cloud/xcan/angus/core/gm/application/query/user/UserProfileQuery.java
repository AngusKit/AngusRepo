package cloud.xcan.angus.core.gm.application.query.user;

import cloud.xcan.angus.api.commonlink.user.User;

/**
 * 用户个人信息查询服务接口 负责用户个人信息的读操作
 */
public interface UserProfileQuery {

  /**
   * 根据用户ID获取个人信息
   */
  User findByUserId(Long userId);
}
