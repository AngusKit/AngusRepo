package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.spec.utils.ObjectUtils.lengthSafe;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.SuccessStatus;
import cloud.xcan.angus.api.commonlink.application.ApplicationMenu;
import cloud.xcan.angus.api.commonlink.oauthuser.AuthenticationUser;
import cloud.xcan.angus.api.commonlink.role.PermissionInfo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.api.pojo.DeviceInfo;
import cloud.xcan.angus.core.gm.domain.authorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.core.gm.domain.user.LoginDevice;
import cloud.xcan.angus.core.gm.domain.user.LoginHistory;
import cloud.xcan.angus.core.gm.infra.authentication.OAuth2ProviderService.OAuthUserInfo;
import cloud.xcan.angus.spec.principal.Principal;
import java.time.LocalDateTime;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * 认证模块应用层转换器 负责领域对象的转换和构造
 */
public class AuthorizationConverter {

  /**
   * 添加用户授权转换
   */
  public static Authorization toUserAddAuthorization(User user, List<Long> roleIds) {
    Authorization authorization = new Authorization();
    authorization.setSubjectType(AuthorizationSubjectType.USER);
    authorization.setSubjectId(user.getId());
    authorization.setSubjectName(user.getName());
    authorization.setRoleIds(roleIds);
    authorization.setStatus(EnabledStatus.ENABLED);
    authorization.setValidFrom(null);
    authorization.setValidTo(null);
    authorization.setDescription("创建用户时授权");
    authorization.setOpened(false);
    authorization.setTenantId(user.getTenantId());
    return authorization;
  }

  public static LoginDevice toLoginDevice(Long userId, LoginDevice device,
      Principal principal, LocalDateTime now) {
    if (device == null) {
      // 如果设备不存在，创建新设备记录
      DeviceInfo deviceInfo = principal.getDeviceInfo();
      device = new LoginDevice();
      device.setUserId(userId);
      device.setDeviceId(deviceInfo.getDeviceId());
      device.setDeviceName(deviceInfo.getDeviceType().getValue());
      device.setDeviceType(deviceInfo.getDeviceType());
      device.setBrowser(deviceInfo.getBrowser());
      device.setBrowserVersion(deviceInfo.getBrowserVersion());
      device.setOs(deviceInfo.getPlatform());
      device.setOsVersion(deviceInfo.getOsVersion());
    }

    // 更新设备信息
    device.setIpAddress(principal.getRemoteAddress());
    device.setUserAgent(principal.getUserAgent());
    device.setIsCurrent(true);
    device.setLastActiveAt(now);
    return device;
  }

  public static LoginHistory toLoginHistory(Long userId, String username,
      SignInType loginType, SuccessStatus loginStatus, String failureReason,
      LocalDateTime now, Principal principal) {
    // 记录登录历史
    LoginHistory loginHistory = new LoginHistory();
    loginHistory.setUserId(userId);
    loginHistory.setUsername(username);
    loginHistory.setLoginTime(now);
    loginHistory.setLoginType(loginType);
    loginHistory.setLoginStatus(loginStatus);
    loginHistory.setIpAddress(principal.getRemoteAddress());
    loginHistory.setUserAgent(principal.getUserAgent());
    if (failureReason != null) {
      loginHistory.setFailureReason(lengthSafe(failureReason, 400));
    }
    // 设置设备信息（简化版，只保存User-Agent）
    if (principal.getDeviceInfo() != null) {
      loginHistory.setDevice(principal.getDeviceInfo().getDeviceType().getValue());
      loginHistory.setDeviceId(principal.getDeviceInfo().getDeviceId());
    }
    return loginHistory;
  }

  /**
   * 从OAuth用户信息创建User实体
   */
  public static User toUserFromOAuth(String username, OAuthUserInfo oauthUserInfo) {
    User user = new User();
    user.setUsername(username);
    user.setName(oauthUserInfo.getNickname() != null ? oauthUserInfo.getNickname() : username);
    user.setEmail(oauthUserInfo.getEmail());
    user.setEmailVerified(true);
    user.setAvatar(oauthUserInfo.getAvatar());
    // OAuth登录的用户不需要密码
    user.setPassword(null);
    return user;
  }

  /**
   * 从User实体创建AuthUser实体
   */
  public static AuthenticationUser toAuthUserFromUser(User user) {
    AuthenticationUser authUser = new AuthenticationUser();
    authUser.setId(String.valueOf(user.getId()));
    authUser.setUsername(user.getUsername());
    authUser.setFullName(user.getName());
    authUser.setPhone(user.getPhone());
    authUser.setEmail(user.getEmail());
    authUser.setPassword(user.getPassword());
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);
    authUser.setTenantId(String.valueOf(user.getTenantId()));
    return authUser;
  }

  /**
   * 使用OAuth用户信息更新User实体
   */
  public static void updateUserFromOAuth(User user, OAuthUserInfo oauthUserInfo) {
    // 更新用户信息（头像、昵称等可能会变化）
    if (oauthUserInfo.getAvatar() != null
        && !oauthUserInfo.getAvatar().equals(user.getAvatar())) {
      user.setAvatar(oauthUserInfo.getAvatar());
    }
    if (oauthUserInfo.getNickname() != null
        && !oauthUserInfo.getNickname().equals(user.getName())) {
      user.setName(oauthUserInfo.getNickname());
    }
    if (oauthUserInfo.getEmail() != null
        && !oauthUserInfo.getEmail().equals(user.getEmail())) {
      user.setEmail(oauthUserInfo.getEmail());
      user.setEmailVerified(true);
    }
  }

  public static void updateAuthUserFromUser(AuthenticationUser user, OAuthUserInfo oauthUserInfo) {
    // 更新用户信息
    if (oauthUserInfo.getNickname() != null
        && !oauthUserInfo.getNickname().equals(user.getName())) {
      user.setFullName(oauthUserInfo.getNickname());
    }
    if (oauthUserInfo.getEmail() != null
        && !oauthUserInfo.getEmail().equals(user.getEmail())) {
      user.setEmail(oauthUserInfo.getEmail());
    }
  }

  public static PermissionInfo toPermissionInfo(ApplicationMenu menu, PermissionInfo permission) {
    PermissionInfo newPermission;
    if (permission != null) {
      // 创建新的权限信息并复制属性
      newPermission = new PermissionInfo();
      newPermission.setParentMenuId(menu.getParentId());
      newPermission.setMenuId(menu.getId());
      newPermission.setMenuName(menu.getName());
      newPermission.setResource(permission.getResource());
      newPermission.setResourceName(permission.getResourceName());
      newPermission.setActions(permission.getActions());
    } else {
      // 创建空的权限信息
      newPermission = new PermissionInfo();
      newPermission.setParentMenuId(menu.getParentId());
      newPermission.setMenuId(menu.getId());
      newPermission.setMenuName(menu.getName());
    }
    return newPermission;
  }
}
