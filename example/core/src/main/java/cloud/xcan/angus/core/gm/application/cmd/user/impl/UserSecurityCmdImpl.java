package cloud.xcan.angus.core.gm.application.cmd.user.impl;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.oauthuser.AuthenticationUser;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserSetting;
import cloud.xcan.angus.api.commonlink.user.enums.UserSettingKey;
import cloud.xcan.angus.api.commonlink.user.model.SecurityValue;
import cloud.xcan.angus.api.pojo.DeviceInfo;
import cloud.xcan.angus.api.pojo.LocationInfo;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserSecurityCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserSettingCmd;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthenticationUserQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserSecurityQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserSettingQuery;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.security.model.PasswordPolicyConfig;
import cloud.xcan.angus.core.gm.domain.user.UserSecurity;
import cloud.xcan.angus.core.gm.domain.user.UserSecurityRepo;
import cloud.xcan.angus.core.gm.domain.user.enums.PasswordStrength;
import cloud.xcan.angus.core.gm.infra.user.TotpService;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.spec.principal.Principal;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户安全命令服务实现
 */
@Service
public class UserSecurityCmdImpl implements UserSecurityCmd {

  @Resource
  private UserSecurityRepo userSecurityRepo;

  @Resource
  private UserSecurityQuery userSecurityQuery;

  @Resource
  private UserQuery userQuery;

  @Resource
  private AuthenticationUserQuery authenticationUserQuery;

  @Resource
  private PasswordEncoder passwordEncoder;

  @Resource
  private TotpService totpService;

  @Resource
  private UserSettingCmd userSettingCmd;

  @Resource
  private UserSettingQuery userSettingQuery;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserSecurity enable2FA(Long userId, String password) {
    return new BizTemplate<UserSecurity>() {
      AuthenticationUser userDb;
      UserSetting settingDb;
      SecurityValue securityValue;

      @Override
      protected void checkParams() {
        // 验证用户存在
        userDb = authenticationUserQuery.findAndCheck(userId);
        // 验证密码
        if (!passwordEncoder.matches(password, userDb.getPassword())) {
          throw ProtocolException.of("密码错误");
        }
        // 获取或创建安全设置
        settingDb = userSettingQuery.findByUserIdAndKey(userId, UserSettingKey.SECURITY);
        securityValue = settingDb.getValue() instanceof SecurityValue
            ? (SecurityValue) settingDb.getValue()
            : new SecurityValue();
        // 验证双因素认证未启用
        if (Boolean.TRUE.equals(securityValue.getTwoFactorEnabled())) {
          throw ProtocolException.of("双因素认证已启用");
        }
      }

      @Override
      protected UserSecurity process() {
        // 生成TOTP密钥和备用码
        String secret = totpService.generateSecret();
        List<String> backupCodes = totpService.generateBackupCodes(8);

        // 保存密钥和备用码（但未启用）到 UserSetting
        securityValue.setTwoFactorSecret(secret);
        securityValue.setBackupCodes(backupCodes);
        securityValue.setTwoFactorEnabled(false); // 待确认后才启用
        userSettingCmd.update(userId, UserSettingKey.SECURITY, securityValue);

        // 返回 UserSecurity 对象（用于兼容性，双因素认证信息已存储在 UserSetting 中）
        UserSecurity security = userSecurityQuery.findOrCreateByUserId(userId);
        // 设置临时字段用于返回（不持久化）
        security.setTwoFactorSecret(secret);
        security.setBackupCodes(backupCodes);
        security.setTwoFactorEnabled(false);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.USER,
            userId,
            userDb.getName(),
            OperationMessage.USER_ENABLE_2FA_DETAILS,
            new Object[]{userDb.getName()}
        );

        return security;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserSecurity confirm2FA(Long userId, String code) {
    return new BizTemplate<UserSecurity>() {
      UserSetting settingDb;
      SecurityValue securityValue;

      @Override
      protected void checkParams() {
        // 获取安全设置
        settingDb = userSettingQuery.findByUserIdAndKey(userId, UserSettingKey.SECURITY);
        securityValue = settingDb.getValue() instanceof SecurityValue
            ? (SecurityValue) settingDb.getValue()
            : new SecurityValue();
        if (securityValue.getTwoFactorSecret() == null) {
          throw ProtocolException.of("请先启用双因素认证");
        }
        // 验证验证码
        if (!totpService.verifyCode(securityValue.getTwoFactorSecret(), code)) {
          throw ProtocolException.of("双因素认证验证码错误");
        }
      }

      @Override
      protected UserSecurity process() {
        // 启用双因素认证
        securityValue.setTwoFactorEnabled(true);
        userSettingCmd.update(userId, UserSettingKey.SECURITY, securityValue);

        // 返回 UserSecurity 对象（用于兼容性，双因素认证信息已存储在 UserSetting 中）
        UserSecurity security = userSecurityQuery.findOrCreateByUserId(userId);
        // 设置临时字段用于返回（不持久化）
        security.setTwoFactorEnabled(true);
        security.setTwoFactorSecret(securityValue.getTwoFactorSecret());

        // 记录操作日志
        User user = userQuery.findAndCheck(userId);
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.USER,
            userId,
            user.getName(),
            OperationMessage.USER_CONFIRM_2FA_DETAILS,
            new Object[]{user.getName()}
        );

        return security;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserSecurity disable2FA(Long userId, String password, String code) {
    return new BizTemplate<UserSecurity>() {
      AuthenticationUser userDb;
      UserSetting settingDb;
      SecurityValue securityValue;

      @Override
      protected void checkParams() {
        // 验证用户存在
        userDb = authenticationUserQuery.findAndCheck(userId);
        // 验证密码
        if (!passwordEncoder.matches(password, userDb.getPassword())) {
          throw ProtocolException.of("密码错误");
        }
        // 获取安全设置
        settingDb = userSettingQuery.findByUserIdAndKey(userId, UserSettingKey.SECURITY);
        securityValue = settingDb.getValue() instanceof SecurityValue
            ? (SecurityValue) settingDb.getValue()
            : new SecurityValue();
        if (!Boolean.TRUE.equals(securityValue.getTwoFactorEnabled())) {
          throw ProtocolException.of("双因素认证未启用");
        }
        // 验证验证码
        if (!totpService.verifyCode(securityValue.getTwoFactorSecret(), code)) {
          throw ProtocolException.of("双因素认证验证码错误");
        }
      }

      @Override
      protected UserSecurity process() {
        // 禁用双因素认证
        securityValue.setTwoFactorEnabled(false);
        securityValue.setTwoFactorSecret(null);
        securityValue.setBackupCodes(null);
        userSettingCmd.update(userId, UserSettingKey.SECURITY, securityValue);

        // 返回 UserSecurity 对象（用于兼容性，双因素认证信息已存储在 UserSetting 中）
        UserSecurity security = userSecurityQuery.findOrCreateByUserId(userId);
        // 设置临时字段用于返回（不持久化）
        security.setTwoFactorEnabled(false);
        security.setTwoFactorSecret(null);
        security.setBackupCodes(null);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.USER,
            userId,
            userDb.getName(),
            OperationMessage.USER_DISABLE_2FA_DETAILS,
            new Object[]{userDb.getName()}
        );

        return security;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserSecurity updateLastLogin(Long userId, Principal principal, LocalDateTime lastLoginAt,
      LocalDateTime passwordLastChanged, PasswordStrength passwordStrength) {
    return new BizTemplate<UserSecurity>() {
      @Override
      protected UserSecurity process() {
        UserSecurity securityDb = userSecurityQuery.findOrCreateByUserId(userId);
        if (passwordLastChanged != null) {
          securityDb.setPasswordLastChanged(passwordLastChanged);
        }
        if (passwordStrength != null) {
          securityDb.setPasswordStrength(passwordStrength);
        }
        if (lastLoginAt != null) {
          securityDb.setLastLoginAt(lastLoginAt);
          LocationInfo locationInfo = principal.getLocationInfo();
          if (locationInfo != null) {
            securityDb.setLastLoginIp(locationInfo.getIp());
            securityDb.setLastLoginLocation(
                locationInfo.getCountry() + " " + locationInfo.getCity());
          }
          DeviceInfo deviceInfo = principal.getDeviceInfo();
          if (deviceInfo != null) {
            securityDb.setLastLoginDevice(deviceInfo.getPlatform());
            securityDb.setLastLoginDeviceId(deviceInfo.getDeviceId());
          }
        }

        return userSecurityRepo.save(securityDb);
      }
    }.execute();
  }

  @Override
  public void savePasswordHistory(PasswordPolicyConfig config, UserSecurity userSecurity,
      String newPasswordEncoded) {
    Integer preventReuse = nullSafe(config.getPreventReuse(), 6);
    List<String> passwordHistory = userSecurity.getPasswordHistory();
    if (passwordHistory == null) {
      passwordHistory = new ArrayList<>();
    }
    // 添加当前密码到历史记录（加密后的）
    passwordHistory.add(0, newPasswordEncoded);
    // 只保留最新的 N 次密码
    if (passwordHistory.size() > preventReuse) {
      passwordHistory = passwordHistory.subList(0, preventReuse);
    }
    userSecurity.setPasswordHistory(passwordHistory);
    userSecurityRepo.save(userSecurity);
  }

}
