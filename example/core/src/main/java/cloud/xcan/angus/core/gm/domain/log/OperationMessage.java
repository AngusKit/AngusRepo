package cloud.xcan.angus.core.gm.domain.log;

/**
 * 用户操作消息常量定义
 * <p>
 * 用于统一管理操作消息，方便后续国际化 消息键格式：operation.{resourceType}.{action}.{key}
 * </p>
 *
 * @author Angus
 */
public interface OperationMessage {

  // ==================== 应用管理相关操作消息 ====================

  /**
   * 应用创建操作详情 参数：{0} = appName (应用名称) 示例：创建应用：系统管理
   */
  String APPLICATION_CREATE_DETAILS = "operation.application.create.details";

  /**
   * 应用更新操作详情 参数：{0} = appName (应用名称) 示例：更新应用：系统管理
   */
  String APPLICATION_UPDATE_DETAILS = "operation.application.update.details";

  /**
   * 应用启用操作详情 参数：{0} = appName (应用名称) 示例：启用应用：系统管理
   */
  String APPLICATION_ENABLE_DETAILS = "operation.application.enable.details";

  /**
   * 应用禁用操作详情 参数：{0} = appName (应用名称) 示例：禁用应用：系统管理
   */
  String APPLICATION_DISABLE_DETAILS = "operation.application.disable.details";

  /**
   * 应用删除操作详情 参数：{0} = appName (应用名称) 示例：删除应用：系统管理
   */
  String APPLICATION_DELETE_DETAILS = "operation.application.delete.details";

  // ==================== 应用菜单管理相关操作消息 ====================

  /**
   * 应用菜单创建操作详情 参数：{0} = menuName (菜单名称), {1} = applicationId (应用ID) 示例：创建应用菜单：用户管理（应用ID：123456）
   */
  String APPLICATION_MENU_CREATE_DETAILS = "operation.application.menu.create.details";

  /**
   * 应用菜单更新操作详情 参数：{0} = menuName (菜单名称), {1} = applicationId (应用ID) 示例：更新应用菜单：用户管理（应用ID：123456）
   */
  String APPLICATION_MENU_UPDATE_DETAILS = "operation.application.menu.update.details";

  /**
   * 应用菜单删除操作详情 参数：{0} = menuName (菜单名称), {1} = applicationId (应用ID) 示例：删除应用菜单：用户管理（应用ID：123456）
   */
  String APPLICATION_MENU_DELETE_DETAILS = "operation.application.menu.delete.details";

  // ==================== 用户管理相关操作消息 ====================

  /**
   * 重置密码操作详情 参数：{0} = userName (用户名称), {1} = account (账号：手机号或邮箱) 示例：重置密码：张三（账号：13800138000）
   */
  String USER_RESET_PASSWORD_DETAILS = "operation.user.reset.password.details";

  /**
   * 修改密码操作详情 参数：{0} = userName (用户名称) 示例：修改密码：张三
   */
  String USER_CHANGE_PASSWORD_DETAILS = "operation.user.change.password.details";

  // ==================== 备份管理相关操作消息 ====================

  /**
   * 备份创建操作详情 参数：{0} = backupName (备份名称) 示例：创建备份：系统备份_20240101
   */
  String BACKUP_CREATE_DETAILS = "operation.config.backup.create.details";

  /**
   * 备份恢复操作详情 参数：{0} = backupName (备份名称) 示例：恢复备份：系统备份_20240101
   */
  String BACKUP_RESTORE_DETAILS = "operation.config.backup.restore.details";

  /**
   * 备份删除操作详情 参数：{0} = backupName (备份名称) 示例：删除备份：系统备份_20240101
   */
  String BACKUP_DELETE_DETAILS = "operation.config.backup.delete.details";

  /**
   * 备份重新运行操作详情 参数：{0} = backupName (备份名称) 示例：重新运行备份：系统备份_20240101
   */
  String BACKUP_RUN_DETAILS = "operation.config.backup.run.details";

  /**
   * 恢复任务创建操作详情 参数：{0} = restoreTaskName (恢复任务名称), {1} = source (恢复源：BACKUP或FILE_PATH)
   * 示例：创建恢复任务：恢复任务_001（来源：备份列表）
   */
  String RESTORE_TASK_CREATE_DETAILS = "operation.config.restore.task.create.details";

  /**
   * 备份计划创建操作详情 参数：{0} = scheduleName (备份计划名称) 示例：创建备份计划：每日备份
   */
  String BACKUP_SCHEDULE_CREATE_DETAILS = "operation.config.backup.schedule.create.details";

  /**
   * 备份计划更新操作详情 参数：{0} = scheduleName (备份计划名称) 示例：更新备份计划：每日备份
   */
  String BACKUP_SCHEDULE_UPDATE_DETAILS = "operation.config.backup.schedule.update.details";

  /**
   * 备份计划启用操作详情 参数：{0} = scheduleName (备份计划名称) 示例：启用备份计划：每日备份
   */
  String BACKUP_SCHEDULE_ENABLE_DETAILS = "operation.config.backup.schedule.enable.details";

  /**
   * 备份计划禁用操作详情 参数：{0} = scheduleName (备份计划名称) 示例：禁用备份计划：每日备份
   */
  String BACKUP_SCHEDULE_DISABLE_DETAILS = "operation.config.backup.schedule.disable.details";

  /**
   * 备份计划删除操作详情 参数：{0} = scheduleName (备份计划名称) 示例：删除备份计划：每日备份
   */
  String BACKUP_SCHEDULE_DELETE_DETAILS = "operation.config.backup.schedule.delete.details";

  // ==================== 部门管理相关操作消息 ====================

  /**
   * 部门创建操作详情 参数：{0} = departmentName (部门名称) 示例：创建部门：技术部
   */
  String DEPARTMENT_CREATE_DETAILS = "operation.organization.department.create.details";

  /**
   * 部门更新操作详情 参数：{0} = departmentName (部门名称) 示例：更新部门：技术部
   */
  String DEPARTMENT_UPDATE_DETAILS = "operation.organization.department.update.details";

  /**
   * 部门启用操作详情 参数：{0} = departmentName (部门名称) 示例：启用部门：技术部
   */
  String DEPARTMENT_ENABLE_DETAILS = "operation.organization.department.enable.details";

  /**
   * 部门禁用操作详情 参数：{0} = departmentName (部门名称) 示例：禁用部门：技术部
   */
  String DEPARTMENT_DISABLE_DETAILS = "operation.organization.department.disable.details";

  /**
   * 部门删除操作详情 参数：{0} = departmentName (部门名称) 示例：删除部门：技术部
   */
  String DEPARTMENT_DELETE_DETAILS = "operation.organization.department.delete.details";

  /**
   * 部门管理者更新操作详情 参数：{0} = departmentName (部门名称), {1} = managerName (管理者名称) 示例：更新部门管理者：技术部（管理者：张三）
   */
  String DEPARTMENT_UPDATE_MANAGER_DETAILS = "operation.organization.department.update.manager.details";

  /**
   * 部门用户添加操作详情 参数：{0} = departmentName (部门名称), {1} = userCount (用户数量) 示例：添加用户到部门：技术部（用户数：3）
   */
  String DEPARTMENT_USER_ADD_DETAILS = "operation.organization.department.user.add.details";

  /**
   * 部门用户移除操作详情 参数：{0} = departmentName (部门名称), {1} = userName (用户名称) 示例：从部门移除用户：技术部（用户：张三）
   */
  String DEPARTMENT_USER_REMOVE_DETAILS = "operation.organization.department.user.remove.details";

  /**
   * 部门用户批量移除操作详情 参数：{0} = departmentName (部门名称), {1} = userCount (用户数量) 示例：从部门批量移除用户：技术部（用户数：3）
   */
  String DEPARTMENT_USER_REMOVE_BATCH_DETAILS = "operation.organization.department.user.remove.batch.details";

  /**
   * 部门用户转移操作详情 参数：{0} = sourceDepartmentName (源部门名称), {1} = targetDepartmentName (目标部门名称), {2} =
   * userCount (用户数量) 示例：转移用户：技术部 -> 研发部（用户数：3）
   */
  String DEPARTMENT_USER_TRANSFER_DETAILS = "operation.organization.department.user.transfer.details";

  /**
   * 设置用户主部门操作详情 参数：{0} = userName (用户名称), {1} = departmentName (部门名称) 示例：设置用户主部门：张三（部门：技术部）
   */
  String DEPARTMENT_USER_SET_PRIMARY_DETAILS = "operation.organization.department.user.set.primary.details";

  // ==================== 邮件管理相关操作消息 ====================

  /**
   * SMTP配置创建操作详情 参数：{0} = smtpInfo (SMTP信息：host:port) 示例：创建SMTP配置：smtp.example.com:587
   */
  String EMAIL_SMTP_CREATE_DETAILS = "operation.config.email.smtp.create.details";

  /**
   * SMTP配置更新操作详情 参数：{0} = smtpInfo (SMTP信息：host:port) 示例：更新SMTP配置：smtp.example.com:587
   */
  String EMAIL_SMTP_UPDATE_DETAILS = "operation.config.email.smtp.update.details";

  /**
   * 邮件模板创建操作详情 参数：{0} = templateName (模板名称), {1} = templateCode (模板编码)
   * 示例：创建邮件模板：欢迎邮件（编码：WELCOME_EMAIL）
   */
  String EMAIL_TEMPLATE_CREATE_DETAILS = "operation.config.email.template.create.details";

  /**
   * 邮件模板更新操作详情 参数：{0} = templateName (模板名称), {1} = templateCode (模板编码)
   * 示例：更新邮件模板：欢迎邮件（编码：WELCOME_EMAIL）
   */
  String EMAIL_TEMPLATE_UPDATE_DETAILS = "operation.config.email.template.update.details";

  /**
   * 邮件模板启用操作详情 参数：{0} = templateName (模板名称) 示例：启用邮件模板：欢迎邮件
   */
  String EMAIL_TEMPLATE_ENABLE_DETAILS = "operation.config.email.template.enable.details";

  /**
   * 邮件模板禁用操作详情 参数：{0} = templateName (模板名称) 示例：禁用邮件模板：欢迎邮件
   */
  String EMAIL_TEMPLATE_DISABLE_DETAILS = "operation.config.email.template.disable.details";

  /**
   * 邮件模板删除操作详情 参数：{0} = templateName (模板名称) 示例：删除邮件模板：欢迎邮件
   */
  String EMAIL_TEMPLATE_DELETE_DETAILS = "operation.config.email.template.delete.details";

  // ==================== 组管理相关操作消息 ====================

  /**
   * 组创建操作详情 参数：{0} = groupName (组名称) 示例：创建组：开发组
   */
  String GROUP_CREATE_DETAILS = "operation.organization.group.create.details";

  /**
   * 组更新操作详情 参数：{0} = groupName (组名称) 示例：更新组：开发组
   */
  String GROUP_UPDATE_DETAILS = "operation.organization.group.update.details";

  /**
   * 组启用操作详情 参数：{0} = groupName (组名称) 示例：启用组：开发组
   */
  String GROUP_ENABLE_DETAILS = "operation.organization.group.enable.details";

  /**
   * 组禁用操作详情 参数：{0} = groupName (组名称) 示例：禁用组：开发组
   */
  String GROUP_DISABLE_DETAILS = "operation.organization.group.disable.details";

  /**
   * 组删除操作详情 参数：{0} = groupName (组名称) 示例：删除组：开发组
   */
  String GROUP_DELETE_DETAILS = "operation.organization.group.delete.details";

  /**
   * 组所有者更新操作详情 参数：{0} = groupName (组名称), {1} = ownerName (所有者名称) 示例：更新组所有者：开发组（所有者：张三）
   */
  String GROUP_UPDATE_OWNER_DETAILS = "operation.organization.group.update.owner.details";

  /**
   * 组用户添加操作详情 参数：{0} = groupName (组名称), {1} = userCount (用户数量) 示例：添加用户到组：开发组（用户数：3）
   */
  String GROUP_USER_ADD_DETAILS = "operation.organization.group.user.add.details";

  /**
   * 组用户移除操作详情 参数：{0} = groupName (组名称), {1} = userName (用户名称) 示例：从组移除用户：开发组（用户：张三）
   */
  String GROUP_USER_REMOVE_DETAILS = "operation.organization.group.user.remove.details";

  /**
   * 组用户批量移除操作详情 参数：{0} = groupName (组名称), {1} = userCount (用户数量) 示例：从组批量移除用户：开发组（用户数：3）
   */
  String GROUP_USER_REMOVE_BATCH_DETAILS = "operation.organization.group.user.remove.batch.details";

  /**
   * 组用户转移操作详情 参数：{0} = sourceGroupName (源组名称), {1} = targetGroupName (目标组名称), {2} = userCount
   * (用户数量) 示例：转移用户：开发组 -> 测试组（用户数：3）
   */
  String GROUP_USER_TRANSFER_DETAILS = "operation.organization.group.user.transfer.details";

  // ==================== LDAP管理相关操作消息 ====================

  /**
   * LDAP配置创建操作详情 参数：{0} = ldapName (LDAP配置名称) 示例：创建LDAP配置：Active Directory
   */
  String LDAP_CREATE_DETAILS = "operation.config.ldap.create.details";

  /**
   * LDAP配置更新操作详情 参数：{0} = ldapName (LDAP配置名称) 示例：更新LDAP配置：Active Directory
   */
  String LDAP_UPDATE_DETAILS = "operation.config.ldap.update.details";

  /**
   * LDAP字段映射更新操作详情 参数：{0} = ldapName (LDAP配置名称) 示例：更新LDAP字段映射：Active Directory
   */
  String LDAP_UPDATE_FIELD_MAPPING_DETAILS = "operation.config.ldap.update.field.mapping.details";

  /**
   * LDAP用户同步操作详情 参数：{0} = ldapName (LDAP配置名称), {1} = totalUsers (总用户数), {2} = newUsers (新用户数), {3}
   * = updatedUsers (更新用户数), {4} = testMode (是否测试模式) 示例：同步LDAP用户：Active
   * Directory（总用户数：100，新用户：10，更新用户：5，测试模式：否）
   */
  String LDAP_SYNC_USERS_DETAILS = "operation.config.ldap.sync.users.details";

  /**
   * LDAP配置删除操作详情 参数：{0} = ldapName (LDAP配置名称) 示例：删除LDAP配置：Active Directory
   */
  String LDAP_DELETE_DETAILS = "operation.config.ldap.delete.details";

  // ==================== 日志保留配置管理相关操作消息 ====================

  /**
   * 日志保留配置更新操作详情 参数：{0} = appName (应用名称), {1} = userLogDays (用户日志保留天数), {2} = systemLogDays
   * (系统日志保留天数), {3} = apiLogDays (API日志保留天数) 示例：更新日志保留配置：系统管理（用户日志：90天，系统日志：60天，API日志：30天）
   */
  String LOG_RETENTION_CONFIG_UPDATE_DETAILS = "operation.config.log.retention.update.details";

  /**
   * 日志保留配置批量更新操作详情 参数：{0} = configCount (配置数量) 示例：批量更新日志保留配置：5个配置
   */
  String LOG_RETENTION_CONFIG_BATCH_UPDATE_DETAILS = "operation.config.log.retention.batch.update.details";

  /**
   * 日志清理操作详情 参数：{0} = appName (应用名称), {1} = totalDeleted (总删除记录数), {2} = sizeFreed (释放空间大小，MB), {3}
   * = dryRun (是否试运行) 示例：清理日志：系统管理（删除记录数：1000，释放空间：50MB，试运行：否）
   */
  String LOG_RETENTION_CLEANUP_DETAILS = "operation.config.log.retention.cleanup.details";

  // ==================== 配额管理相关操作消息 ====================

  /**
   * 配额更新操作详情 参数：{0} = quotaName (配额名称), {1} = limit (限额), {2} = unit (单位)
   * 示例：更新配额：用户数量（限额：1000，单位：个）
   */
  String QUOTA_UPDATE_DETAILS = "operation.quota.update.details";

  /**
   * 配额批量更新限额操作详情 参数：{0} = quotaCount (配额数量) 示例：批量更新配额限额：5个配额
   */
  String QUOTA_BATCH_UPDATE_LIMITS_DETAILS = "operation.quota.batch.update.limits.details";

  /**
   * 配额状态更新操作详情 参数：{0} = quotaName (配额名称), {1} = enabled (是否启用) 示例：更新配额状态：用户数量（启用：是）
   */
  String QUOTA_UPDATE_STATUS_DETAILS = "operation.quota.update.status.details";

  // ==================== 角色管理相关操作消息 ====================

  /**
   * 角色创建操作详情 参数：{0} = roleName (角色名称) 示例：创建角色：管理员
   */
  String ROLE_CREATE_DETAILS = "operation.permission.role.create.details";

  /**
   * 角色更新操作详情 参数：{0} = roleName (角色名称) 示例：更新角色：管理员
   */
  String ROLE_UPDATE_DETAILS = "operation.permission.role.update.details";

  /**
   * 角色状态更新操作详情 参数：{0} = roleName (角色名称), {1} = status (状态) 示例：更新角色状态：管理员（启用）
   */
  String ROLE_UPDATE_STATUS_DETAILS = "operation.permission.role.update.status.details";

  /**
   * 角色删除操作详情 参数：{0} = roleName (角色名称) 示例：删除角色：管理员
   */
  String ROLE_DELETE_DETAILS = "operation.permission.role.delete.details";

  /**
   * 角色权限更新操作详情 参数：{0} = roleName (角色名称), {1} = permissionCount (权限数量) 示例：更新角色权限：管理员（权限数量：5）
   */
  String ROLE_UPDATE_PERMISSIONS_DETAILS = "operation.permission.role.update.permissions.details";

  /**
   * 角色设置默认操作详情 参数：{0} = roleName (角色名称), {1} = isDefault (是否默认) 示例：设置角色默认：管理员（默认：是）
   */
  String ROLE_SET_DEFAULT_DETAILS = "operation.permission.role.set.default.details";

  // ==================== 安全配置管理相关操作消息 ====================

  /**
   * 密码策略更新操作详情 参数：{0} = securityName (安全配置名称) 示例：更新密码策略：密码策略
   */
  String SECURITY_UPDATE_PASSWORD_POLICY_DETAILS = "operation.config.security.update.password.policy.details";

  /**
   * 登录安全配置更新操作详情 参数：{0} = securityName (安全配置名称) 示例：更新登录安全配置：登录安全
   */
  String SECURITY_UPDATE_LOGIN_SECURITY_DETAILS = "operation.config.security.update.login.security.details";

  /**
   * IP白名单添加操作详情 参数：{0} = securityName (安全配置名称) 示例：添加IP白名单：IP白名单
   */
  String SECURITY_ADD_IP_WHITELIST_DETAILS = "operation.config.security.add.ip.whitelist.details";

  /**
   * IP白名单更新操作详情 参数：{0} = securityName (安全配置名称) 示例：更新IP白名单：IP白名单
   */
  String SECURITY_UPDATE_IP_WHITELIST_DETAILS = "operation.config.security.update.ip.whitelist.details";

  /**
   * IP白名单删除操作详情 参数：{0} = securityName (安全配置名称) 示例：删除IP白名单：IP白名单
   */
  String SECURITY_DELETE_IP_WHITELIST_DETAILS = "operation.config.security.delete.ip.whitelist.details";

  /**
   * 安全通知配置更新操作详情 参数：{0} = securityName (安全配置名称) 示例：更新安全通知配置：安全通知
   */
  String SECURITY_UPDATE_NOTIFICATION_CONFIG_DETAILS = "operation.config.security.update.notification.config.details";

  // ==================== 系统设置管理相关操作消息 ====================

  /**
   * 备份设置更新操作详情 参数：{0} = settingKey (设置键) 示例：更新备份设置：BACKUP_SETTINGS
   */
  String SETTING_UPDATE_BACKUP_DETAILS = "operation.config.setting.update.backup.details";

  /**
   * 告警规则设置更新操作详情 参数：{0} = settingKey (设置键) 示例：更新告警规则设置：ALERT_RULES
   */
  String SETTING_UPDATE_ALERT_RULES_DETAILS = "operation.config.setting.update.alert.rules.details";

  /**
   * Eureka配置更新操作详情 参数：{0} = settingKey (设置键) 示例：更新Eureka配置：EUREKA_CONFIG
   */
  String SETTING_UPDATE_EUREKA_CONFIG_DETAILS = "operation.config.setting.update.eureka.config.details";

  // ==================== SMS服务商管理相关操作消息 ====================

  /**
   * SMS服务商更新操作详情 参数：{0} = providerName (服务商名称) 示例：更新SMS服务商：阿里云
   */
  String SMS_PROVIDER_UPDATE_DETAILS = "operation.config.sms.provider.update.details";

  /**
   * SMS服务商状态更新操作详情 参数：{0} = providerName (服务商名称), {1} = status (状态) 示例：更新SMS服务商状态：阿里云（启用）
   */
  String SMS_PROVIDER_UPDATE_STATUS_DETAILS = "operation.config.sms.provider.update.status.details";

  /**
   * SMS服务商设置默认操作详情 参数：{0} = providerName (服务商名称) 示例：设置SMS服务商默认：阿里云
   */
  String SMS_PROVIDER_SET_DEFAULT_DETAILS = "operation.config.sms.provider.set.default.details";

  // ==================== SMS模板管理相关操作消息 ====================

  /**
   * SMS模板创建操作详情 参数：{0} = templateName (模板名称) 示例：创建SMS模板：验证码模板
   */
  String SMS_TEMPLATE_CREATE_DETAILS = "operation.config.sms.template.create.details";

  /**
   * SMS模板更新操作详情 参数：{0} = templateName (模板名称) 示例：更新SMS模板：验证码模板
   */
  String SMS_TEMPLATE_UPDATE_DETAILS = "operation.config.sms.template.update.details";

  /**
   * SMS模板状态更新操作详情 参数：{0} = templateName (模板名称), {1} = status (状态) 示例：更新SMS模板状态：验证码模板（启用）
   */
  String SMS_TEMPLATE_UPDATE_STATUS_DETAILS = "operation.config.sms.template.update.status.details";

  /**
   * SMS模板删除操作详情 参数：{0} = templateName (模板名称) 示例：删除SMS模板：验证码模板
   */
  String SMS_TEMPLATE_DELETE_DETAILS = "operation.config.sms.template.delete.details";

  // ==================== 标签分类管理相关操作消息 ====================

  /**
   * 标签分类创建操作详情 参数：{0} = categoryName (分类名称) 示例：创建标签分类：产品分类
   */
  String TAG_CATEGORY_CREATE_DETAILS = "operation.tag.category.create.details";

  /**
   * 标签分类更新操作详情 参数：{0} = categoryName (分类名称) 示例：更新标签分类：产品分类
   */
  String TAG_CATEGORY_UPDATE_DETAILS = "operation.tag.category.update.details";

  /**
   * 标签分类删除操作详情 参数：{0} = categoryName (分类名称) 示例：删除标签分类：产品分类
   */
  String TAG_CATEGORY_DELETE_DETAILS = "operation.tag.category.delete.details";

  // ==================== 标签管理相关操作消息 ====================

  /**
   * 标签创建操作详情 参数：{0} = tagName (标签名称) 示例：创建标签：重要
   */
  String TAG_CREATE_DETAILS = "operation.tag.create.details";

  /**
   * 标签更新操作详情 参数：{0} = tagName (标签名称) 示例：更新标签：重要
   */
  String TAG_UPDATE_DETAILS = "operation.tag.update.details";

  /**
   * 标签删除操作详情 参数：{0} = tagName (标签名称) 示例：删除标签：重要
   */
  String TAG_DELETE_DETAILS = "operation.tag.delete.details";

  // ==================== 租户管理相关操作消息 ====================

  /**
   * 租户创建操作详情 参数：{0} = tenantName (租户名称), {1} = accountType (账号类型) 示例：创建租户：测试公司（主账号）
   */
  String TENANT_CREATE_DETAILS = "operation.tenant.create.details";

  /**
   * 租户更新操作详情 参数：{0} = tenantName (租户名称) 示例：更新租户：测试公司
   */
  String TENANT_UPDATE_DETAILS = "operation.tenant.update.details";

  /**
   * 租户启用操作详情 参数：{0} = tenantName (租户名称) 示例：启用租户：测试公司
   */
  String TENANT_ENABLE_DETAILS = "operation.tenant.enable.details";

  /**
   * 租户禁用操作详情 参数：{0} = tenantName (租户名称) 示例：禁用租户：测试公司
   */
  String TENANT_DISABLE_DETAILS = "operation.tenant.disable.details";

  /**
   * 租户删除操作详情 参数：{0} = tenantName (租户名称) 示例：删除租户：测试公司
   */
  String TENANT_DELETE_DETAILS = "operation.tenant.delete.details";

  // ==================== 用户令牌管理相关操作消息 ====================

  /**
   * 用户令牌创建操作详情 参数：{0} = tokenName (令牌名称) 示例：创建用户令牌：API访问令牌
   */
  String USER_TOKEN_CREATE_DETAILS = "operation.user.token.create.details";

  /**
   * 用户令牌更新操作详情 参数：{0} = tokenName (令牌名称) 示例：更新用户令牌：API访问令牌
   */
  String USER_TOKEN_UPDATE_DETAILS = "operation.user.token.update.details";

  /**
   * 用户令牌撤销操作详情 参数：{0} = tokenName (令牌名称) 示例：撤销用户令牌：API访问令牌
   */
  String USER_TOKEN_REVOKE_DETAILS = "operation.user.token.revoke.details";

  /**
   * 用户令牌删除操作详情 参数：{0} = tokenName (令牌名称) 示例：删除用户令牌：API访问令牌
   */
  String USER_TOKEN_DELETE_DETAILS = "operation.user.token.delete.details";

  // ==================== 用户安全相关操作消息 ====================

  /**
   * 用户启用双因素认证操作详情 参数：{0} = userName (用户名称) 示例：启用双因素认证：张三
   */
  String USER_ENABLE_2FA_DETAILS = "operation.user.security.enable.2fa.details";

  /**
   * 用户确认双因素认证操作详情 参数：{0} = userName (用户名称) 示例：确认双因素认证：张三
   */
  String USER_CONFIRM_2FA_DETAILS = "operation.user.security.confirm.2fa.details";

  /**
   * 用户禁用双因素认证操作详情 参数：{0} = userName (用户名称) 示例：禁用双因素认证：张三
   */
  String USER_DISABLE_2FA_DETAILS = "operation.user.security.disable.2fa.details";

  // ==================== 用户个人资料相关操作消息 ====================

  /**
   * 用户更新个人资料操作详情 参数：{0} = userName (用户名称) 示例：更新个人资料：张三
   */
  String USER_UPDATE_PROFILE_DETAILS = "operation.user.profile.update.details";

  /**
   * 用户更新头像操作详情 参数：{0} = userName (用户名称) 示例：更新头像：张三
   */
  String USER_UPDATE_AVATAR_DETAILS = "operation.user.profile.update.avatar.details";

  /**
   * 用户删除头像操作详情 参数：{0} = userName (用户名称) 示例：删除头像：张三
   */
  String USER_DELETE_AVATAR_DETAILS = "operation.user.profile.delete.avatar.details";

  // ==================== 用户邀请相关操作消息 ====================

  /**
   * 用户邀请创建操作详情 参数：{0} = email (邀请邮箱) 示例：创建用户邀请：test@example.com
   */
  String USER_INVITE_CREATE_DETAILS = "operation.user.invite.create.details";

  /**
   * 用户邀请取消操作详情 参数：{0} = email (邀请邮箱) 示例：取消用户邀请：test@example.com
   */
  String USER_INVITE_CANCEL_DETAILS = "operation.user.invite.cancel.details";

  /**
   * 用户邀请重新发送操作详情 参数：{0} = email (邀请邮箱) 示例：重新发送用户邀请：test@example.com
   */
  String USER_INVITE_RESEND_DETAILS = "operation.user.invite.resend.details";

  /**
   * 用户邀请拒绝操作详情 参数：{0} = inviteIdentifier (邀请标识：邮箱或链接邀请) 示例：拒绝用户邀请：test@example.com
   */
  String USER_INVITE_REJECT_DETAILS = "operation.user.invite.reject.details";

  // ==================== 用户管理相关操作消息 ====================

  /**
   * 用户创建操作详情 参数：{0} = userName (用户名称) 示例：创建用户：张三
   */
  String USER_CREATE_DETAILS = "operation.user.create.details";

  /**
   * 用户更新操作详情 参数：{0} = userName (用户名称) 示例：更新用户：张三
   */
  String USER_UPDATE_DETAILS = "operation.user.update.details";

  /**
   * 用户启用操作详情 参数：{0} = userName (用户名称) 示例：启用用户：张三
   */
  String USER_ENABLE_DETAILS = "operation.user.enable.details";

  /**
   * 用户禁用操作详情 参数：{0} = userName (用户名称) 示例：禁用用户：张三
   */
  String USER_DISABLE_DETAILS = "operation.user.disable.details";

  /**
   * 用户锁定操作详情 参数：{0} = userName (用户名称) 示例：锁定用户：张三
   */
  String USER_LOCK_DETAILS = "operation.user.lock.details";

  /**
   * 用户解锁操作详情 参数：{0} = userName (用户名称) 示例：解锁用户：张三
   */
  String USER_UNLOCK_DETAILS = "operation.user.unlock.details";

  /**
   * 用户删除操作详情 参数：{0} = userName (用户名称) 示例：删除用户：张三
   */
  String USER_DELETE_DETAILS = "operation.user.delete.details";

  /**
   * 用户批量删除操作详情 参数：{0} = userCount (用户数量) 示例：批量删除用户：5个用户
   */
  String USER_BATCH_DELETE_DETAILS = "operation.user.batch.delete.details";

}
