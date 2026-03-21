package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.spec.utils.ObjectUtils.lengthSafe;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.enums.UserSource;
import cloud.xcan.angus.api.commonlink.user.enums.UserStatus;
import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.domain.ldap.LdapSyncHistory;
import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapSyncStatus;
import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapSyncType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LdapConverter {

  /**
   * 从LDAP信息创建用户
   */
  public static User createUserFromLdap(String username, String name, String email, String phone,
      String jobTitle, Ldap config) {
    User user = new User();
    user.setUsername(username);
    user.setName(name != null ? name : username);
    user.setEmail(email);
    user.setEmailVerified(true);
    user.setPhone(phone);
    user.setPhoneVerified(true);
    user.setJobTitle(jobTitle);
    user.setStatus(UserStatus.ACTIVE);
    user.setSysAdmin(false);
    user.setLocked(false);
    // 设置用户来源为LDAP同步
    user.setSource(UserSource.LDAP_SYNC);
    // 关联LDAP配置ID
    user.setLdapId(config.getId());
    // 生成随机密码（LDAP用户通过LDAP认证，不需要本地密码）
    user.setPassword(generateRandomPassword());
    user.setTenantId(config.getTenantId());
    return user;
  }

  /**
   * 生成随机密码
   */
  public static String generateRandomPassword() {
    // 生成一个随机密码（LDAP用户通过LDAP认证，本地密码仅作为占位符）
    return "LDAP_USER_" + System.currentTimeMillis();
  }

  /**
   * 从LDAP信息更新用户
   */
  public static void updateUserFromLdap(User user, String name, String email, String phone,
      String jobTitle) {
    if (name != null && !name.isEmpty()) {
      user.setName(name);
    }
    if (email != null && !email.isEmpty()) {
      user.setEmail(email);
      user.setEmailVerified(true);
    }
    if (phone != null && !phone.isEmpty()) {
      user.setPhone(phone);
      user.setPhoneVerified(true);
    }
    if (jobTitle != null) {
      user.setJobTitle(jobTitle);
    }
  }

  /**
   * 获取搜索属性列表
   */
  public static String[] getSearchAttributes(Map<String, String> fieldMapping) {
    List<String> attributes = new ArrayList<>();
    if (fieldMapping != null) {
      attributes.add(fieldMapping.getOrDefault("uid", "uid"));
      attributes.add(fieldMapping.getOrDefault("cn", "cn"));
      attributes.add(fieldMapping.getOrDefault("mail", "mail"));
      attributes.add(fieldMapping.getOrDefault("department", "department"));
      attributes.add(fieldMapping.getOrDefault("title", "title"));
      attributes.add(fieldMapping.getOrDefault("mobile", "mobile"));
    } else {
      attributes.add("uid");
      attributes.add("cn");
      attributes.add("mail");
      attributes.add("department");
      attributes.add("title");
      attributes.add("mobile");
    }
    return attributes.toArray(new String[0]);
  }

  /**
   * 从LDAP配置创建同步历史记录
   */
  public static LdapSyncHistory createSyncHistory(Ldap config) {
    LdapSyncHistory history = new LdapSyncHistory();
    history.setLdapId(config.getId());
    history.setStatus(LdapSyncStatus.RUNNING);
    history.setSyncType(LdapSyncType.MANUAL);
    history.setStartTime(LocalDateTime.now());
    history.setTotalUsers(0);
    history.setNewUsers(0);
    history.setUpdatedUsers(0);
    history.setDeletedUsers(0);
    history.setFailedUsers(0);
    return history;
  }

  public static void toHistory(LdapSyncHistory history, LdapSyncStatus syncStatus,
      String errorMessage, LocalDateTime startTime, int totalUsers, int newUsers, int updatedUsers,
      int deletedUsers, int failedUsersCount) {
    LocalDateTime endTime = LocalDateTime.now();
    Duration duration = Duration.between(startTime, endTime);
    history.setEndTime(endTime);
    history.setDuration((int) duration.getSeconds());
    history.setTotalUsers(totalUsers);
    history.setNewUsers(newUsers);
    history.setUpdatedUsers(updatedUsers);
    history.setDeletedUsers(deletedUsers);
    history.setFailedUsers(failedUsersCount);
    history.setStatus(syncStatus);
    history.setErrorMessage(lengthSafe(errorMessage, 1000));
  }

}
