package cloud.xcan.angus.core.gm.application.query.user.impl;

import cloud.xcan.angus.api.commonlink.user.UserSetting;
import cloud.xcan.angus.api.commonlink.user.enums.UserSettingKey;
import cloud.xcan.angus.api.commonlink.user.model.SecurityValue;
import cloud.xcan.angus.core.gm.application.query.user.UserSecurityQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserSettingQuery;
import cloud.xcan.angus.core.gm.domain.user.UserSecurity;
import cloud.xcan.angus.core.gm.domain.user.UserSecurityRepo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 用户安全查询服务实现
 */
@Service
public class UserSecurityQueryImpl implements UserSecurityQuery {

  @Resource
  private UserSecurityRepo userSecurityRepo;

  @Resource
  private UserSettingQuery userSettingQuery;

  @Override
  public UserSecurity findByUserId(Long userId) {
    UserSecurity security = userSecurityRepo.findByUserId(userId);
    // 从 UserSetting 中读取双因素认证信息并合并
    UserSetting setting = userSettingQuery.findByUserIdAndKey(userId, UserSettingKey.SECURITY);
    if (setting != null && setting.getValue() instanceof SecurityValue securityValue) {
      if (security == null) {
        security = new UserSecurity();
        security.setUserId(userId);
      }
      // 合并双因素认证信息
      security.setTwoFactorEnabled(securityValue.getTwoFactorEnabled());
      security.setTwoFactorSecret(securityValue.getTwoFactorSecret());
      security.setBackupCodes(securityValue.getBackupCodes());
    }
    return security;
  }

  @Override
  public UserSecurity findOrCreateByUserId(Long userId) {
    UserSecurity security = userSecurityRepo.findByUserId(userId);
    if (security == null) {
      security = new UserSecurity();
      security.setId(userId);
      security.setUserId(userId);
      // 从 UserSetting 中读取双因素认证信息
      UserSetting setting = userSettingQuery.findByUserIdAndKey(userId, UserSettingKey.SECURITY);
      if (setting != null && setting.getValue() instanceof SecurityValue securityValue) {
        security.setTwoFactorEnabled(securityValue.getTwoFactorEnabled());
        security.setTwoFactorSecret(securityValue.getTwoFactorSecret());
        security.setBackupCodes(securityValue.getBackupCodes());
      } else {
        security.setTwoFactorEnabled(false);
      }
      // 保存以获取ID
      security = userSecurityRepo.save(security);
    } else {
      // 从 UserSetting 中读取双因素认证信息并合并
      UserSetting setting = userSettingQuery.findByUserIdAndKey(userId, UserSettingKey.SECURITY);
      if (setting != null && setting.getValue() instanceof SecurityValue securityValue) {
        security.setTwoFactorEnabled(securityValue.getTwoFactorEnabled());
        security.setTwoFactorSecret(securityValue.getTwoFactorSecret());
        security.setBackupCodes(securityValue.getBackupCodes());
      }
    }
    return security;
  }
}
