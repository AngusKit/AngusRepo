package cloud.xcan.angus.core.gm.application.query.user;

import cloud.xcan.angus.core.gm.domain.user.UserSecurity;

/**
 * 用户安全查询服务接口 负责用户安全的读操作
 */
public interface UserSecurityQuery {

  /**
   * 根据用户ID获取安全信息
   */
  UserSecurity findByUserId(Long userId);

  /**
   * 根据用户ID获取或创建安全信息
   */
  UserSecurity findOrCreateByUserId(Long userId);
}
