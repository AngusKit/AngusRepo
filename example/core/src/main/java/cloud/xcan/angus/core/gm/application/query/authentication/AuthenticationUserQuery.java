package cloud.xcan.angus.core.gm.application.query.authentication;

import cloud.xcan.angus.api.commonlink.oauthuser.AuthenticationUser;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.enums.SignInType;
import java.util.List;

/**
 * 认证用户查询服务接口 负责处理认证用户相关的查询操作
 */
public interface AuthenticationUserQuery {

  /**
   * 根据账号和密码查找用户
   */
  List<AuthenticationUser> findByAccountAndPassword(String account, String password);

  /**
   * 验证用户密码
   */
  void checkPassword(Long id, String password);

  /**
   * 根据账号查找并验证用户
   */
  AuthenticationUser checkAndFindByAccount(Long userId, SignInType signinType, String account,
      String password);

  /**
   * 检查是否旧历史密码
   */
  void checkHistoryPasswordExists(Long userId, String newPassword);

  /**
   * 验证用户状态是否有效
   */
  void checkUserValid(AuthenticationUser user);

  /**
   * 检查并查找用户
   */
  AuthenticationUser findAndCheck(Long id);

  /**
   * 根据用户名查找用户
   */
  AuthenticationUser findByUsername(String username);

  /**
   * 根据邮箱查找用户列表
   */
  List<AuthenticationUser> findByEmail(String email);

  /**
   * 根据手机号查找用户列表
   */
  List<AuthenticationUser> findByMobile(String mobile);

  /**
   * 使用LDAP服务验证密码
   */
  boolean verifyLdapPassword(User user, String password);
}
