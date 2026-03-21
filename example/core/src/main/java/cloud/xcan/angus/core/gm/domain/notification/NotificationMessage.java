package cloud.xcan.angus.core.gm.domain.notification;

/**
 * 通知消息常量定义
 * <p>
 * 用于统一管理通知消息，方便后续国际化 消息键格式：notification.{category}.{type}.{key}
 * </p>
 *
 * @author Angus
 */
public interface NotificationMessage {

  // ==================== AngusGM 业务分类常量 ====================
  String CATEGORY_USER_MANAGEMENT = "用户管理";
  String CATEGORY_TENANT_MANAGEMENT = "租户管理";
  String CATEGORY_APPLICATION_MANAGEMENT = "应用管理";
  String CATEGORY_DEPARTMENT_MANAGEMENT = "部门管理";
  String CATEGORY_GROUP_MANAGEMENT = "组管理";
  String CATEGORY_ROLE_MANAGEMENT = "角色管理";
  String CATEGORY_AUTHORIZATION_MANAGEMENT = "授权管理";
  String CATEGORY_EMAIL_MANAGEMENT = "邮件管理";
  String CATEGORY_SMS_MANAGEMENT = "短信管理";
  String CATEGORY_BACKUP_MANAGEMENT = "备份管理";
  String CATEGORY_LDAP_MANAGEMENT = "LDAP管理";
  String CATEGORY_QUOTA_MANAGEMENT = "配额管理";
  String CATEGORY_SECURITY_MANAGEMENT = "安全配置";
  String CATEGORY_SYSTEM_ALERT = "系统告警";
  String CATEGORY_SYSTEM_MONITORING = "系统监控";
  String CATEGORY_SYSTEM_VERSION = "系统版本";
  String CATEGORY_TAG_MANAGEMENT = "标签管理";
  String CATEGORY_INTERFACE_MANAGEMENT = "接口管理";

  // ==================== 用户管理相关消息 ====================

  /**
   * 用户创建标题 参数：{0} = userName (用户名称) 示例：用户创建成功
   */
  String USER_CREATED_TITLE = "notification.user.created.title";

  /**
   * 用户创建描述 参数：{0} = userName (用户名称), {1} = username (用户名) 示例：用户「张三」已创建，用户名：zhangsan
   */
  String USER_CREATED_DESCRIPTION = "notification.user.created.description";

  /**
   * 用户更新标题 参数：{0} = userName (用户名称) 示例：用户信息已更新
   */
  String USER_UPDATED_TITLE = "notification.user.updated.title";

  /**
   * 用户更新描述 参数：{0} = userName (用户名称) 示例：用户「张三」的信息已更新
   */
  String USER_UPDATED_DESCRIPTION = "notification.user.updated.description";

  /**
   * 用户删除标题 参数：{0} = userName (用户名称) 示例：用户已删除
   */
  String USER_DELETED_TITLE = "notification.user.deleted.title";

  /**
   * 用户删除描述 参数：{0} = userName (用户名称) 示例：用户「张三」已被删除
   */
  String USER_DELETED_DESCRIPTION = "notification.user.deleted.description";

  /**
   * 用户锁定标题 参数：{0} = userName (用户名称) 示例：用户已锁定
   */
  String USER_LOCKED_TITLE = "notification.user.locked.title";

  /**
   * 用户锁定描述 参数：{0} = userName (用户名称) 示例：用户「张三」已被锁定
   */
  String USER_LOCKED_DESCRIPTION = "notification.user.locked.description";

  /**
   * 用户解锁标题 参数：{0} = userName (用户名称) 示例：用户已解锁
   */
  String USER_UNLOCKED_TITLE = "notification.user.unlocked.title";

  /**
   * 用户解锁描述 参数：{0} = userName (用户名称) 示例：用户「张三」已解锁
   */
  String USER_UNLOCKED_DESCRIPTION = "notification.user.unlocked.description";

  /**
   * 用户密码重置标题 参数：无 示例：密码重置成功
   */
  String USER_PASSWORD_RESET_TITLE = "notification.user.password.reset.title";

  /**
   * 用户密码重置描述 参数：无 示例：您的密码已重置，请使用新密码登录
   */
  String USER_PASSWORD_RESET_DESCRIPTION = "notification.user.password.reset.description";

  /**
   * 用户邀请标题 参数：{0} = email (邮箱) 示例：您收到了加入邀请
   */
  String USER_INVITED_TITLE = "notification.user.invited.title";

  /**
   * 用户邀请描述 参数：{0} = email (邮箱), {1} = tenantName (租户名称) 示例：您收到了加入租户「{1}」的邀请，邮箱：{0}
   */
  String USER_INVITED_DESCRIPTION = "notification.user.invited.description";

  /**
   * 用户登录失败标题 参数：{0} = username (用户名) 示例：用户登录失败
   */
  String USER_LOGIN_FAILED_TITLE = "notification.user.login.failed.title";

  /**
   * 用户登录失败描述 参数：{0} = username (用户名), {1} = errorCount (失败次数) 示例：用户「{0}」登录失败，已连续失败{1}次
   */
  String USER_LOGIN_FAILED_DESCRIPTION = "notification.user.login.failed.description";

  /**
   * 用户第三方登录失败标题 参数：{0} = providerName (第三方提供商名称) 示例：第三方登录失败
   */
  String USER_OAUTH_LOGIN_FAILED_TITLE = "notification.user.oauth.login.failed.title";

  /**
   * 用户第三方登录失败描述 参数：{0} = providerName (第三方提供商名称), {1} = openId (第三方用户ID), {2} = errorMessage (错误信息)
   * 示例：{0}登录失败，用户ID：{1}，错误：{2}
   */
  String USER_OAUTH_LOGIN_FAILED_DESCRIPTION = "notification.user.oauth.login.failed.description";

  /**
   * 用户注册成功标题 参数：{0} = userName (用户名称) 示例：用户注册成功
   */
  String USER_REGISTER_SUCCESS_TITLE = "notification.user.register.success.title";

  /**
   * 用户注册成功描述 参数：{0} = userName (用户名称) 示例：用户「{0}」注册成功
   */
  String USER_REGISTER_SUCCESS_DESCRIPTION = "notification.user.register.success.description";

  // ==================== 租户管理相关消息 ====================

  /**
   * 租户创建标题 参数：{0} = tenantName (租户名称) 示例：租户创建成功
   */
  String TENANT_CREATED_TITLE = "notification.tenant.created.title";

  /**
   * 租户创建描述 参数：{0} = tenantName (租户名称), {1} = tenantCode (租户编码) 示例：租户「{0}」已创建，编码：{1}
   */
  String TENANT_CREATED_DESCRIPTION = "notification.tenant.created.description";

  /**
   * 租户更新标题 参数：{0} = tenantName (租户名称) 示例：租户信息已更新
   */
  String TENANT_UPDATED_TITLE = "notification.tenant.updated.title";

  /**
   * 租户更新描述 参数：{0} = tenantName (租户名称) 示例：租户「{0}」的信息已更新
   */
  String TENANT_UPDATED_DESCRIPTION = "notification.tenant.updated.description";

  /**
   * 租户启用标题 参数：{0} = tenantName (租户名称) 示例：租户已启用
   */
  String TENANT_ENABLED_TITLE = "notification.tenant.enabled.title";

  /**
   * 租户启用描述 参数：{0} = tenantName (租户名称) 示例：租户「{0}」已启用
   */
  String TENANT_ENABLED_DESCRIPTION = "notification.tenant.enabled.description";

  /**
   * 租户禁用标题 参数：{0} = tenantName (租户名称) 示例：租户已禁用
   */
  String TENANT_DISABLED_TITLE = "notification.tenant.disabled.title";

  /**
   * 租户禁用描述 参数：{0} = tenantName (租户名称) 示例：租户「{0}」已禁用
   */
  String TENANT_DISABLED_DESCRIPTION = "notification.tenant.disabled.description";

  /**
   * 租户过期提醒标题 参数：{0} = tenantName (租户名称) 示例：租户即将过期
   */
  String TENANT_EXPIRING_TITLE = "notification.tenant.expiring.title";

  /**
   * 租户过期提醒描述 参数：{0} = tenantName (租户名称), {1} = expireDate (过期日期) 示例：租户「{0}」将于{1}过期，请及时续费
   */
  String TENANT_EXPIRING_DESCRIPTION = "notification.tenant.expiring.description";

  // ==================== 应用管理相关消息 ====================

  /**
   * 应用创建标题 参数：{0} = appName (应用名称) 示例：应用创建成功
   */
  String APPLICATION_CREATED_TITLE = "notification.application.created.title";

  /**
   * 应用创建描述 参数：{0} = appName (应用名称), {1} = appCode (应用编码) 示例：应用「{0}」已创建，编码：{1}
   */
  String APPLICATION_CREATED_DESCRIPTION = "notification.application.created.description";

  /**
   * 应用更新标题 参数：{0} = appName (应用名称) 示例：应用信息已更新
   */
  String APPLICATION_UPDATED_TITLE = "notification.application.updated.title";

  /**
   * 应用更新描述 参数：{0} = appName (应用名称) 示例：应用「{0}」的信息已更新
   */
  String APPLICATION_UPDATED_DESCRIPTION = "notification.application.updated.description";

  /**
   * 应用启用标题 参数：{0} = appName (应用名称) 示例：应用已启用
   */
  String APPLICATION_ENABLED_TITLE = "notification.application.enabled.title";

  /**
   * 应用启用描述 参数：{0} = appName (应用名称) 示例：应用「{0}」已启用
   */
  String APPLICATION_ENABLED_DESCRIPTION = "notification.application.enabled.description";

  /**
   * 应用禁用标题 参数：{0} = appName (应用名称) 示例：应用已禁用
   */
  String APPLICATION_DISABLED_TITLE = "notification.application.disabled.title";

  /**
   * 应用禁用描述 参数：{0} = appName (应用名称) 示例：应用「{0}」已禁用
   */
  String APPLICATION_DISABLED_DESCRIPTION = "notification.application.disabled.description";

  /**
   * 应用删除标题 参数：{0} = appName (应用名称) 示例：应用已删除
   */
  String APPLICATION_DELETED_TITLE = "notification.application.deleted.title";

  /**
   * 应用删除描述 参数：{0} = appName (应用名称) 示例：应用「{0}」已被删除
   */
  String APPLICATION_DELETED_DESCRIPTION = "notification.application.deleted.description";

  // ==================== 部门管理相关消息 ====================

  /**
   * 部门创建标题 参数：{0} = deptName (部门名称) 示例：部门创建成功
   */
  String DEPARTMENT_CREATED_TITLE = "notification.department.created.title";

  /**
   * 部门创建描述 参数：{0} = deptName (部门名称), {1} = deptCode (部门编码) 示例：部门「{0}」已创建，编码：{1}
   */
  String DEPARTMENT_CREATED_DESCRIPTION = "notification.department.created.description";

  /**
   * 部门更新标题 参数：{0} = deptName (部门名称) 示例：部门信息已更新
   */
  String DEPARTMENT_UPDATED_TITLE = "notification.department.updated.title";

  /**
   * 部门更新描述 参数：{0} = deptName (部门名称) 示例：部门「{0}」的信息已更新
   */
  String DEPARTMENT_UPDATED_DESCRIPTION = "notification.department.updated.description";

  /**
   * 部门删除标题 参数：{0} = deptName (部门名称) 示例：部门已删除
   */
  String DEPARTMENT_DELETED_TITLE = "notification.department.deleted.title";

  /**
   * 部门删除描述 参数：{0} = deptName (部门名称) 示例：部门「{0}」已被删除
   */
  String DEPARTMENT_DELETED_DESCRIPTION = "notification.department.deleted.description";

  /**
   * 部门成员添加标题 参数：{0} = deptName (部门名称) 示例：您已加入部门
   */
  String DEPARTMENT_MEMBER_ADDED_TITLE = "notification.department.member.added.title";

  /**
   * 部门成员添加描述 参数：{0} = deptName (部门名称) 示例：您已加入部门「{0}」
   */
  String DEPARTMENT_MEMBER_ADDED_DESCRIPTION = "notification.department.member.added.description";

  /**
   * 部门成员移除标题 参数：{0} = deptName (部门名称) 示例：您已从部门移除
   */
  String DEPARTMENT_MEMBER_REMOVED_TITLE = "notification.department.member.removed.title";

  /**
   * 部门成员移除描述 参数：{0} = deptName (部门名称) 示例：您已从部门「{0}」移除
   */
  String DEPARTMENT_MEMBER_REMOVED_DESCRIPTION = "notification.department.member.removed.description";

  // ==================== 组管理相关消息 ====================

  /**
   * 组创建标题 参数：{0} = groupName (组名称) 示例：组创建成功
   */
  String GROUP_CREATED_TITLE = "notification.group.created.title";

  /**
   * 组创建描述 参数：{0} = groupName (组名称), {1} = groupCode (组编码) 示例：组「{0}」已创建，编码：{1}
   */
  String GROUP_CREATED_DESCRIPTION = "notification.group.created.description";

  /**
   * 组更新标题 参数：{0} = groupName (组名称) 示例：组信息已更新
   */
  String GROUP_UPDATED_TITLE = "notification.group.updated.title";

  /**
   * 组更新描述 参数：{0} = groupName (组名称) 示例：组「{0}」的信息已更新
   */
  String GROUP_UPDATED_DESCRIPTION = "notification.group.updated.description";

  /**
   * 组删除标题 参数：{0} = groupName (组名称) 示例：组已删除
   */
  String GROUP_DELETED_TITLE = "notification.group.deleted.title";

  /**
   * 组删除描述 参数：{0} = groupName (组名称) 示例：组「{0}」已被删除
   */
  String GROUP_DELETED_DESCRIPTION = "notification.group.deleted.description";

  /**
   * 组成员添加标题 参数：{0} = groupName (组名称) 示例：您已加入组
   */
  String GROUP_MEMBER_ADDED_TITLE = "notification.group.member.added.title";

  /**
   * 组成员添加描述 参数：{0} = groupName (组名称) 示例：您已加入组「{0}」
   */
  String GROUP_MEMBER_ADDED_DESCRIPTION = "notification.group.member.added.description";

  /**
   * 组成员移除标题 参数：{0} = groupName (组名称) 示例：您已从组移除
   */
  String GROUP_MEMBER_REMOVED_TITLE = "notification.group.member.removed.title";

  /**
   * 组成员移除描述 参数：{0} = groupName (组名称) 示例：您已从组「{0}」移除
   */
  String GROUP_MEMBER_REMOVED_DESCRIPTION = "notification.group.member.removed.description";

  // ==================== 角色管理相关消息 ====================

  /**
   * 角色创建标题 参数：{0} = roleName (角色名称) 示例：角色创建成功
   */
  String ROLE_CREATED_TITLE = "notification.role.created.title";

  /**
   * 角色创建描述 参数：{0} = roleName (角色名称), {1} = roleCode (角色编码) 示例：角色「{0}」已创建，编码：{1}
   */
  String ROLE_CREATED_DESCRIPTION = "notification.role.created.description";

  /**
   * 角色更新标题 参数：{0} = roleName (角色名称) 示例：角色信息已更新
   */
  String ROLE_UPDATED_TITLE = "notification.role.updated.title";

  /**
   * 角色更新描述 参数：{0} = roleName (角色名称) 示例：角色「{0}」的信息已更新
   */
  String ROLE_UPDATED_DESCRIPTION = "notification.role.updated.description";

  /**
   * 角色删除标题 参数：{0} = roleName (角色名称) 示例：角色已删除
   */
  String ROLE_DELETED_TITLE = "notification.role.deleted.title";

  /**
   * 角色删除描述 参数：{0} = roleName (角色名称) 示例：角色「{0}」已被删除
   */
  String ROLE_DELETED_DESCRIPTION = "notification.role.deleted.description";

  /**
   * 角色权限更新标题 参数：{0} = roleName (角色名称) 示例：角色权限已更新
   */
  String ROLE_PERMISSIONS_UPDATED_TITLE = "notification.role.permissions.updated.title";

  /**
   * 角色权限更新描述 参数：{0} = roleName (角色名称) 示例：角色「{0}」的权限已更新
   */
  String ROLE_PERMISSIONS_UPDATED_DESCRIPTION = "notification.role.permissions.updated.description";

  // ==================== 授权管理相关消息 ====================

  /**
   * 授权创建标题 参数：{0} = subjectName (授权主体名称) 示例：授权创建成功
   */
  String AUTHORIZATION_CREATED_TITLE = "notification.authorization.created.title";

  /**
   * 授权创建描述 参数：{0} = subjectName (授权主体名称), {1} = appAndRoleInfo (应用和角色信息) 示例：为「{0}」创建了授权，{1}
   */
  String AUTHORIZATION_CREATED_DESCRIPTION = "notification.authorization.created.description";

  /**
   * 授权更新标题 参数：{0} = subjectName (授权主体名称) 示例：授权信息已更新
   */
  String AUTHORIZATION_UPDATED_TITLE = "notification.authorization.updated.title";

  /**
   * 授权更新描述 参数：{0} = subjectName (授权主体名称), {1} = appAndRoleInfo (应用和角色信息) 示例：为「{0}」的授权信息已更新，{1}
   */
  String AUTHORIZATION_UPDATED_DESCRIPTION = "notification.authorization.updated.description";

  /**
   * 授权删除标题 参数：{0} = subjectName (授权主体名称) 示例：授权已删除
   */
  String AUTHORIZATION_DELETED_TITLE = "notification.authorization.deleted.title";

  /**
   * 授权删除描述 参数：{0} = subjectName (授权主体名称), {1} = appName (应用名称) 示例：为「{0}」的应用「{1}」授权已删除
   */
  String AUTHORIZATION_DELETED_DESCRIPTION = "notification.authorization.deleted.description";

  /**
   * 授权启用标题 参数：{0} = subjectName (授权主体名称) 示例：授权已启用
   */
  String AUTHORIZATION_ENABLED_TITLE = "notification.authorization.enabled.title";

  /**
   * 授权启用描述 参数：{0} = subjectName (授权主体名称) 示例：为「{0}」的授权已启用
   */
  String AUTHORIZATION_ENABLED_DESCRIPTION = "notification.authorization.enabled.description";

  /**
   * 授权禁用标题 参数：{0} = subjectName (授权主体名称) 示例：授权已禁用
   */
  String AUTHORIZATION_DISABLED_TITLE = "notification.authorization.disabled.title";

  /**
   * 授权禁用描述 参数：{0} = subjectName (授权主体名称) 示例：为「{0}」的授权已禁用
   */
  String AUTHORIZATION_DISABLED_DESCRIPTION = "notification.authorization.disabled.description";

  // ==================== 邮件管理相关消息 ====================

  /**
   * 邮件发送成功标题 参数：无 示例：邮件发送成功
   */
  String EMAIL_SENT_SUCCESS_TITLE = "notification.email.sent.success.title";

  /**
   * 邮件发送成功描述 参数：{0} = subject (邮件主题) 示例：邮件「{0}」已成功发送
   */
  String EMAIL_SENT_SUCCESS_DESCRIPTION = "notification.email.sent.success.description";

  /**
   * 邮件发送失败标题 参数：无 示例：邮件发送失败
   */
  String EMAIL_SENT_FAILED_TITLE = "notification.email.sent.failed.title";

  /**
   * 邮件发送失败描述 参数：{0} = subject (邮件主题), {1} = errorMessage (错误信息) 示例：邮件「{0}」发送失败：{1}
   */
  String EMAIL_SENT_FAILED_DESCRIPTION = "notification.email.sent.failed.description";

  /**
   * 邮件模板创建标题 参数：{0} = templateName (模板名称) 示例：邮件模板创建成功
   */
  String EMAIL_TEMPLATE_CREATED_TITLE = "notification.email.template.created.title";

  /**
   * 邮件模板创建描述 参数：{0} = templateName (模板名称), {1} = templateCode (模板编码) 示例：邮件模板「{0}」已创建，编码：{1}
   */
  String EMAIL_TEMPLATE_CREATED_DESCRIPTION = "notification.email.template.created.description";

  // ==================== 短信管理相关消息 ====================

  /**
   * 短信发送成功标题 参数：无 示例：短信发送成功
   */
  String SMS_SENT_SUCCESS_TITLE = "notification.sms.sent.success.title";

  /**
   * 短信发送成功描述 参数：{0} = phone (手机号) 示例：短信已成功发送至{0}
   */
  String SMS_SENT_SUCCESS_DESCRIPTION = "notification.sms.sent.success.description";

  /**
   * 短信发送失败标题 参数：无 示例：短信发送失败
   */
  String SMS_SENT_FAILED_TITLE = "notification.sms.sent.failed.title";

  /**
   * 短信发送失败描述 参数：{0} = phone (手机号), {1} = errorMessage (错误信息) 示例：短信发送至{0}失败：{1}
   */
  String SMS_SENT_FAILED_DESCRIPTION = "notification.sms.sent.failed.description";

  /**
   * 短信模板创建标题 参数：{0} = templateName (模板名称) 示例：短信模板创建成功
   */
  String SMS_TEMPLATE_CREATED_TITLE = "notification.sms.template.created.title";

  /**
   * 短信模板创建描述 参数：{0} = templateName (模板名称), {1} = templateCode (模板编码) 示例：短信模板「{0}」已创建，编码：{1}
   */
  String SMS_TEMPLATE_CREATED_DESCRIPTION = "notification.sms.template.created.description";

  // ==================== 备份管理相关消息 ====================

  /**
   * 备份创建标题 参数：{0} = backupName (备份名称) 示例：备份任务已创建
   */
  String BACKUP_CREATED_TITLE = "notification.backup.created.title";

  /**
   * 备份创建描述 参数：{0} = backupName (备份名称) 示例：备份任务「{0}」已创建
   */
  String BACKUP_CREATED_DESCRIPTION = "notification.backup.created.description";

  /**
   * 备份完成标题 参数：{0} = backupName (备份名称) 示例：备份任务完成
   */
  String BACKUP_COMPLETED_TITLE = "notification.backup.completed.title";

  /**
   * 备份完成描述 参数：{0} = backupName (备份名称), {1} = fileSize (文件大小) 示例：备份任务「{0}」已完成，文件大小：{1}
   */
  String BACKUP_COMPLETED_DESCRIPTION = "notification.backup.completed.description";

  /**
   * 备份失败标题 参数：{0} = backupName (备份名称) 示例：备份任务失败
   */
  String BACKUP_FAILED_TITLE = "notification.backup.failed.title";

  /**
   * 备份失败描述 参数：{0} = backupName (备份名称), {1} = errorMessage (错误信息) 示例：备份任务「{0}」执行失败：{1}
   */
  String BACKUP_FAILED_DESCRIPTION = "notification.backup.failed.description";

  /**
   * 恢复任务完成标题 参数：{0} = restoreTaskName (恢复任务名称) 示例：恢复任务完成
   */
  String RESTORE_COMPLETED_TITLE = "notification.restore.completed.title";

  /**
   * 恢复任务完成描述 参数：{0} = restoreTaskName (恢复任务名称) 示例：恢复任务「{0}」已完成
   */
  String RESTORE_COMPLETED_DESCRIPTION = "notification.restore.completed.description";

  /**
   * 恢复任务失败标题 参数：{0} = restoreTaskName (恢复任务名称) 示例：恢复任务失败
   */
  String RESTORE_FAILED_TITLE = "notification.restore.failed.title";

  /**
   * 恢复任务失败描述 参数：{0} = restoreTaskName (恢复任务名称), {1} = errorMessage (错误信息) 示例：恢复任务「{0}」执行失败：{1}
   */
  String RESTORE_FAILED_DESCRIPTION = "notification.restore.failed.description";

  // ==================== LDAP管理相关消息 ====================

  /**
   * LDAP配置创建标题 参数：{0} = ldapName (LDAP配置名称) 示例：LDAP配置创建成功
   */
  String LDAP_CREATED_TITLE = "notification.ldap.created.title";

  /**
   * LDAP配置创建描述 参数：{0} = ldapName (LDAP配置名称) 示例：LDAP配置「{0}」已创建
   */
  String LDAP_CREATED_DESCRIPTION = "notification.ldap.created.description";

  /**
   * LDAP同步完成标题 参数：{0} = ldapName (LDAP配置名称) 示例：LDAP同步完成
   */
  String LDAP_SYNC_COMPLETED_TITLE = "notification.ldap.sync.completed.title";

  /**
   * LDAP同步完成描述 参数：{0} = ldapName (LDAP配置名称), {1} = newUsers (新增用户数), {2} = updatedUsers (更新用户数)
   * 示例：LDAP配置「{0}」同步完成，新增用户{1}个，更新用户{2}个
   */
  String LDAP_SYNC_COMPLETED_DESCRIPTION = "notification.ldap.sync.completed.description";

  /**
   * LDAP同步失败标题 参数：{0} = ldapName (LDAP配置名称) 示例：LDAP同步失败
   */
  String LDAP_SYNC_FAILED_TITLE = "notification.ldap.sync.failed.title";

  /**
   * LDAP同步失败描述 参数：{0} = ldapName (LDAP配置名称), {1} = errorMessage (错误信息) 示例：LDAP配置「{0}」同步失败：{1}
   */
  String LDAP_SYNC_FAILED_DESCRIPTION = "notification.ldap.sync.failed.description";

  // ==================== 配额管理相关消息 ====================

  /**
   * 配额使用警告标题 参数：{0} = quotaName (配额名称) 示例：配额使用警告
   */
  String QUOTA_USAGE_WARNING_TITLE = "notification.quota.usage.warning.title";

  /**
   * 配额使用警告描述 参数：{0} = quotaName (配额名称), {1} = usedValue (已使用值), {2} = limitValue (限制值), {3} =
   * usagePercent (使用百分比) 示例：配额「{0}」使用率已达{3}%，已使用{1}/{2}
   */
  String QUOTA_USAGE_WARNING_DESCRIPTION = "notification.quota.usage.warning.description";

  /**
   * 配额超限标题 参数：{0} = quotaName (配额名称) 示例：配额已超限
   */
  String QUOTA_EXCEEDED_TITLE = "notification.quota.exceeded.title";

  /**
   * 配额超限描述 参数：{0} = quotaName (配额名称), {1} = usedValue (已使用值), {2} = limitValue (限制值)
   * 示例：配额「{0}」已超限，已使用{1}，限制值{2}
   */
  String QUOTA_EXCEEDED_DESCRIPTION = "notification.quota.exceeded.description";

  // ==================== 安全配置相关消息 ====================

  /**
   * 安全配置更新标题 参数：{0} = securityName (安全配置名称) 示例：安全配置已更新
   */
  String SECURITY_CONFIG_UPDATED_TITLE = "notification.security.config.updated.title";

  /**
   * 安全配置更新描述 参数：{0} = securityName (安全配置名称) 示例：安全配置「{0}」已更新
   */
  String SECURITY_CONFIG_UPDATED_DESCRIPTION = "notification.security.config.updated.description";

  /**
   * 安全告警标题 参数：{0} = alertType (告警类型) 示例：安全告警
   */
  String SECURITY_ALERT_TITLE = "notification.security.alert.title";

  /**
   * 安全告警描述 参数：{0} = alertType (告警类型), {1} = description (描述) 示例：检测到安全告警：{0}，{1}
   */
  String SECURITY_ALERT_DESCRIPTION = "notification.security.alert.description";

  // ==================== 系统告警相关消息 ====================

  /**
   * 系统告警触发标题 参数：{0} = ruleName (告警规则名称) 示例：系统告警触发
   */
  String SYSTEM_ALERT_TRIGGERED_TITLE = "notification.system.alert.triggered.title";

  /**
   * 系统告警触发描述 参数：{0} = ruleName (告警规则名称), {1} = metric (监控指标), {2} = currentValue (当前值), {3} =
   * threshold (阈值) 示例：告警规则「{0}」已触发，指标{1}当前值{2}超过阈值{3}
   */
  String SYSTEM_ALERT_TRIGGERED_DESCRIPTION = "notification.system.alert.triggered.description";

  /**
   * 系统告警恢复标题 参数：{0} = ruleName (告警规则名称) 示例：系统告警已恢复
   */
  String SYSTEM_ALERT_RESOLVED_TITLE = "notification.system.alert.resolved.title";

  /**
   * 系统告警恢复描述 参数：{0} = ruleName (告警规则名称), {1} = metric (监控指标) 示例：告警规则「{0}」的指标{1}已恢复正常
   */
  String SYSTEM_ALERT_RESOLVED_DESCRIPTION = "notification.system.alert.resolved.description";

  // ==================== 系统版本相关消息 ====================

  /**
   * 系统版本更新标题 参数：{0} = version (版本号) 示例：系统版本更新
   */
  String SYSTEM_VERSION_UPDATED_TITLE = "notification.system.version.updated.title";

  /**
   * 系统版本更新描述 参数：{0} = version (版本号), {1} = appCode (应用编码) 示例：应用「{1}」已更新至版本{0}
   */
  String SYSTEM_VERSION_UPDATED_DESCRIPTION = "notification.system.version.updated.description";

  /**
   * 系统版本发布标题 参数：{0} = version (版本号) 示例：新版本发布
   */
  String SYSTEM_VERSION_RELEASED_TITLE = "notification.system.version.released.title";

  /**
   * 系统版本发布描述 参数：{0} = version (版本号), {1} = appCode (应用编码), {2} = releaseDate (发布日期)
   * 示例：应用「{1}」新版本{0}已于{2}发布
   */
  String SYSTEM_VERSION_RELEASED_DESCRIPTION = "notification.system.version.released.description";

  // ==================== 标签管理相关消息 ====================

  /**
   * 标签创建标题 参数：{0} = tagName (标签名称) 示例：标签创建成功
   */
  String TAG_CREATED_TITLE = "notification.tag.created.title";

  /**
   * 标签创建描述 参数：{0} = tagName (标签名称) 示例：标签「{0}」已创建
   */
  String TAG_CREATED_DESCRIPTION = "notification.tag.created.description";

  /**
   * 标签更新标题 参数：{0} = tagName (标签名称) 示例：标签信息已更新
   */
  String TAG_UPDATED_TITLE = "notification.tag.updated.title";

  /**
   * 标签更新描述 参数：{0} = tagName (标签名称) 示例：标签「{0}」的信息已更新
   */
  String TAG_UPDATED_DESCRIPTION = "notification.tag.updated.description";

  /**
   * 标签删除标题 参数：{0} = tagName (标签名称) 示例：标签已删除
   */
  String TAG_DELETED_TITLE = "notification.tag.deleted.title";

  /**
   * 标签删除描述 参数：{0} = tagName (标签名称) 示例：标签「{0}」已被删除
   */
  String TAG_DELETED_DESCRIPTION = "notification.tag.deleted.description";

  // ==================== 接口管理相关消息 ====================

  /**
   * 接口同步完成标题 参数：{0} = serviceName (服务名称) 示例：接口同步完成
   */
  String INTERFACE_SYNC_COMPLETED_TITLE = "notification.interface.sync.completed.title";

  /**
   * 接口同步完成描述 参数：{0} = serviceName (服务名称), {1} = syncCount (同步数量) 示例：服务「{0}」的接口同步完成，共同步{1}个接口
   */
  String INTERFACE_SYNC_COMPLETED_DESCRIPTION = "notification.interface.sync.completed.description";

  /**
   * 接口同步失败标题 参数：{0} = serviceName (服务名称) 示例：接口同步失败
   */
  String INTERFACE_SYNC_FAILED_TITLE = "notification.interface.sync.failed.title";

  /**
   * 接口同步失败描述 参数：{0} = serviceName (服务名称), {1} = errorMessage (错误信息) 示例：服务「{0}」的接口同步失败：{1}
   */
  String INTERFACE_SYNC_FAILED_DESCRIPTION = "notification.interface.sync.failed.description";

  /**
   * 接口调用异常标题 参数：{0} = interfacePath (接口路径) 示例：接口调用异常
   */
  String INTERFACE_CALL_ERROR_TITLE = "notification.interface.call.error.title";

  /**
   * 接口调用异常描述 参数：{0} = interfacePath (接口路径), {1} = errorCode (错误代码), {2} = errorMessage (错误信息)
   * 示例：接口「{0}」调用异常，错误代码：{1}，错误信息：{2}
   */
  String INTERFACE_CALL_ERROR_DESCRIPTION = "notification.interface.call.error.description";

  // ==================== 系统监控相关消息 ====================

  /**
   * 系统负载高标题 参数：{0} = resourceType (资源类型：CPU/内存/磁盘) 示例：系统负载告警
   */
  String SYSTEM_LOAD_HIGH_TITLE = "notification.system.load.high.title";

  /**
   * 系统负载高描述 参数：{0} = resourceType (资源类型：CPU/内存/磁盘), {1} = usagePercent (使用率)
   * 示例：{0}使用率已达到{1}%，超过85%阈值
   */
  String SYSTEM_LOAD_HIGH_DESCRIPTION = "notification.system.load.high.description";

  /**
   * 服务组件异常标题 参数：无 示例：服务组件状态异常
   */
  String SERVICE_COMPONENT_ABNORMAL_TITLE = "notification.service.component.abnormal.title";

  /**
   * 服务组件异常描述 参数：{0} = componentDetails (异常组件详情) 示例：检测到以下组件状态异常：{0}
   */
  String SERVICE_COMPONENT_ABNORMAL_DESCRIPTION = "notification.service.component.abnormal.description";
}
