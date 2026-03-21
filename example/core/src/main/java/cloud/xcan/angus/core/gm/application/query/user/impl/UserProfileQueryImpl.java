package cloud.xcan.angus.core.gm.application.query.user.impl;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.gm.application.query.user.UserProfileQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 用户个人信息查询服务实现
 */
@Service
public class UserProfileQueryImpl implements UserProfileQuery {

  @Resource
  private UserQuery userQuery;

  @Override
  public User findByUserId(Long userId) {
    return userQuery.findAndCheck(userId);
  }
}
