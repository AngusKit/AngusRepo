package cloud.xcan.angus.core.gm.application.cmd.user;

import cloud.xcan.angus.core.gm.domain.security.model.PasswordPolicyConfig;
import cloud.xcan.angus.core.gm.domain.user.UserSecurity;
import cloud.xcan.angus.core.gm.domain.user.enums.PasswordStrength;
import cloud.xcan.angus.spec.principal.Principal;
import java.time.LocalDateTime;

/**
 * 用户安全命令服务接口 负责用户安全的写操作
 */
public interface UserSecurityCmd {

  /**
   * 启用双因素认证（生成密钥和备用码）
   */
  UserSecurity enable2FA(Long userId, String password);

  /**
   * 确认启用双因素认证
   */
  UserSecurity confirm2FA(Long userId, String code);

  /**
   * 禁用双因素认证
   */
  UserSecurity disable2FA(Long userId, String password, String code);

  /**
   * 更新最后登录信息
   */
  UserSecurity updateLastLogin(Long userId, Principal principal, LocalDateTime lastLoginAt,
      LocalDateTime passwordLastChanged, PasswordStrength passwordStrength);

  /**
   * 保存密码历史
   */
  void savePasswordHistory(PasswordPolicyConfig config, UserSecurity userSecurity,
      String newPasswordEncoded);
}
