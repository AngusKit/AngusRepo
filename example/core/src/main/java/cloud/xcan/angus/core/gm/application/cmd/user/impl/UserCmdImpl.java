package cloud.xcan.angus.core.gm.application.cmd.user.impl;

import static cloud.xcan.angus.core.gm.application.converter.AuthorizationConverter.toUserAddAuthorization;
import static cloud.xcan.angus.core.gm.infra.utils.RegistrationInfoGenerator.generateUserName;
import static cloud.xcan.angus.core.gm.infra.utils.RegistrationInfoGenerator.generateUsername;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.get;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.QuotaConstant;
import cloud.xcan.angus.api.commonlink.oauthuser.AuthenticationUser;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.commonlink.user.enums.UserSource;
import cloud.xcan.angus.api.commonlink.user.enums.UserStatus;
import cloud.xcan.angus.api.manager.QuotaManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.authentication.AuthenticationUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.authorization.AuthorizationCmd;
import cloud.xcan.angus.core.gm.application.cmd.department.DepartmentUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.converter.AuthorizationConverter;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthenticationUserQuery;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.authorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserCmdImpl extends CommCmd<User, Long> implements UserCmd {

  @Resource
  private UserRepo userRepo;

  @Resource
  private UserQuery userQuery;

  @Resource
  private GroupUserCmd groupUserCmd;

  @Resource
  private DepartmentUserCmd departmentUserCmd;

  @Resource
  private AuthenticationUserQuery authenticationUserQuery;

  @Resource
  private AuthenticationUserCmd authenticationUserCmd;

  @Resource
  private AuthorizationCmd authorizationCmd;

  @Resource
  private PasswordEncoder passwordEncoder;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Resource
  private TenantQuery tenantQuery;

  @Resource
  private QuotaManager quotaManager;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public User create(User user) {
    return new BizTemplate<User>() {
      @Override
      protected void checkParams() {
        // 防止添加重复用户
        if (isNotEmpty(user.getUsername()) && userQuery.existsByUsername(user.getUsername())) {
          throw ResourceExisted.of("用户名「{0}」已存在", new Object[]{user.getUsername()});
        }
        if (isNotEmpty(user.getEmail()) && userQuery.existsByEmail(user.getEmail())) {
          throw ResourceExisted.of("邮箱「{0}」已存在", new Object[]{user.getEmail()});
        }
        if (isNotEmpty(user.getPhone()) && userQuery.existsByPhone(user.getPhone())) {
          throw ResourceExisted.of("手机号「{0}」已存在", new Object[]{user.getPhone()});
        }
      }

      @Override
      protected User process() {
        // 保存用户
        create0(user);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.USER,
            user.getId(),
            user.getName(),
            OperationMessage.USER_CREATE_DETAILS,
            new Object[]{user.getName()}
        );
        return user;
      }
    }.execute();
  }

  @Override
  public User create0(User user) {
    // 0. 防止添加重复用户
    if (isNotEmpty(user.getUsername()) && userQuery.existsByUsername(user.getUsername())) {
      throw ResourceExisted.of("用户名「{0}」已存在", new Object[]{user.getUsername()});
    }

    // 1. 增加用户配额使用量
    if (user.getTenantId() == null) {
      user.setTenantId(getOptTenantId());
    }
    quotaManager.increaseTenantQuota(
        tenantQuery.getMainTenantOfSameAccount(user.getTenantId()).getId(),
        QuotaConstant.QuotaUserCount, 1L);

    // 2. 保存用户信息
    if (isEmpty(user.getUsername())) {
      user.setUsername(generateUsername(user.getEmail(), user.getPhone()));
    }
    if (isEmpty(user.getName())) {
      user.setName(generateUserName(user.getEmail(), user.getPhone()));
    }
    insert(user);

    // 3. 创建AuthUser实体（用于OAuth2认证，设置认证相关字段）
    AuthenticationUser authUser = saveAuthenticationUser(user);

    // 4. 保存用户与角色关联
    if (isNotEmpty(user.getRoleIds())) {
      Authorization authorization = toUserAddAuthorization(user, user.getRoleIds());
      authorizationCmd.create(authorization);
    }

    // 5. 保存用户与部门关联
    if (user.getDepartmentId() != null) {
      departmentUserCmd.addUsers(user.getDepartmentId(), List.of(user.getId()));
    }

    // 6. 关联认证用户到User实体（用于页面展示）
    user.setAuthUser(authUser);
    return user;
  }

  private AuthenticationUser saveAuthenticationUser(User user) {
    if (user.getPassword() != null) {
      // LDAP同步用户：存储 {LDAP-PROXY}userId:username 格式，供 OAuth2 流程使用 LdapPasswordConnection 验证
      if (UserSource.LDAP_SYNC.equals(user.getSource())) {
        user.setPassword(
            "{LDAP-PROXY}" + user.getId() + ":" + (user.getUsername() != null ? user.getUsername()
                : ""));
      } else {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // 加密密码
      }
    }
    AuthenticationUser authUser = AuthorizationConverter.toAuthUserFromUser(user);
    authenticationUserCmd.create0(authUser);
    return authUser;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public User update(User user) {
    return new BizTemplate<User>() {
      User existing;
      AuthenticationUser authUserDb;

      @Override
      protected void checkParams() {
        existing = userQuery.findAndCheck(user.getId());
        authUserDb = authenticationUserQuery.findAndCheck(user.getId());
        if (isNotEmpty(user.getUsername()) && !user.getUsername().equals(existing.getUsername())) {
          if (userQuery.existsByUsernameAndIdNot(user.getUsername(), user.getId())) {
            throw ResourceExisted.of("用户名「{0}」已存在", new Object[]{user.getUsername()});
          }
        }
        if (isNotEmpty(user.getEmail()) && !user.getEmail().equals(existing.getEmail())) {
          if (userQuery.existsByEmail(user.getEmail())) {
            throw ResourceExisted.of("邮箱「{0}」已存在", new Object[]{user.getEmail()});
          }
        }
        if (isNotEmpty(user.getPhone()) && !user.getPhone().equals(existing.getPhone())) {
          if (userQuery.existsByPhone(user.getPhone())) {
            throw ResourceExisted.of("手机号「{0}」已存在", new Object[]{user.getPhone()});
          }
        }
      }

      @Override
      protected User process() {
        // 检查是否修改部门
        if (existing.getDepartmentId() == null && user.getDepartmentId() != null) {
          // 添加部门
          departmentUserCmd.addUsers(user.getDepartmentId(), List.of(user.getId()));
        } else if (existing.getDepartmentId() != null && user.getDepartmentId() == null) {
          // 清除部门
          departmentUserCmd.removeUser(existing.getDepartmentId(), user.getId());
        } else if (existing.getDepartmentId() != null
            && !Objects.equals(existing.getDepartmentId(), user.getDepartmentId())) {
          // 修改部门
          departmentUserCmd.transferUsers(
              existing.getDepartmentId(), user.getDepartmentId(), List.of(user.getId()));
        }

        // 保存用户信息变更前的值，用于判断是否需要同步更新认证用户
        String oldUsername = existing.getUsername();
        String oldName = existing.getName();
        String oldEmail = existing.getEmail();
        Boolean emailVerified = Objects.equals(existing.getEmail(), user.getEmail())
            && existing.getEmailVerified();
        String oldPhone = existing.getPhone();
        Boolean phoneVerified = Objects.equals(existing.getPhone(), user.getPhone())
            && existing.getPhoneVerified();

        CoreUtils.copyPropertiesIgnoreNull(user, existing);
        existing.setEmailVerified(emailVerified);
        existing.setPhoneVerified(phoneVerified);
        userRepo.save(existing);

        // 同步修改认证用户对应字段
        boolean needUpdate = false;
        if (user.getUsername() != null && !user.getUsername().equals(oldUsername)) {
          authUserDb.setUsername(existing.getUsername());
          // LDAP用户：密码字段存储 {LDAP-PROXY}userId:username，需同步更新
          if (UserSource.LDAP_SYNC.equals(existing.getSource())) {
            authUserDb.setPassword(
                "{LDAP-PROXY}" + existing.getId() + ":" + existing.getUsername());
          }
          needUpdate = true;
        }
        if (user.getName() != null && !user.getName().equals(oldName)) {
          authUserDb.setFullName(existing.getName());
          needUpdate = true;
        }
        if (user.getEmail() != null && !user.getEmail().equals(oldEmail)) {
          authUserDb.setEmail(existing.getEmail());
          needUpdate = true;
        }
        if (user.getPhone() != null && !user.getPhone().equals(oldPhone)) {
          authUserDb.setPhone(existing.getPhone());
          needUpdate = true;
        }

        if (needUpdate) {
          authenticationUserCmd.update0(authUserDb);
        }

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.USER,
            existing.getId(),
            existing.getName(),
            OperationMessage.USER_UPDATE_DETAILS,
            new Object[]{existing.getName()}
        );
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public User updateEnableStatus(Long id, EnabledStatus status) {
    return new BizTemplate<User>() {
      User existing;
      AuthenticationUser authUserDb;

      @Override
      protected void checkParams() {
        existing = userQuery.findAndCheck(id);
        authUserDb = authenticationUserQuery.findAndCheck(id);
        // 禁止禁用系统内置用户（系统管理员）
        if (Boolean.TRUE.equals(existing.getSysAdmin()) && EnabledStatus.DISABLED.equals(status)) {
          throw ProtocolException.of("禁止禁用系统管理员「{0}」", new Object[]{existing.getName()});
        }
        if (existing.getStatus().isPending()) {
          throw ProtocolException.of("邀请用户未接受", new Object[]{existing.getName()});
        }
      }

      @Override
      protected User process() {
        if (status.isEnabled()) {
          existing.setStatus(UserStatus.ACTIVE);
        } else {
          existing.setStatus(UserStatus.DISABLED);
        }
        User saved = userRepo.save(existing);

        authUserDb.setEnabled(EnabledStatus.ENABLED.equals(status));
        authenticationUserCmd.update0(authUserDb);

        // 记录操作日志
        if (status.isEnabled()) {
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.USER,
              saved.getId(),
              saved.getName(),
              OperationMessage.USER_ENABLE_DETAILS,
              new Object[]{saved.getName()}
          );
        } else {
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.USER,
              saved.getId(),
              saved.getName(),
              OperationMessage.USER_DISABLE_DETAILS,
              new Object[]{saved.getName()}
          );
        }

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public User updateLockStatus(Long id, Boolean isLocked) {
    return new BizTemplate<User>() {
      User existing;
      AuthenticationUser authUserDb;

      @Override
      protected void checkParams() {
        existing = userQuery.findAndCheck(id);
        authUserDb = authenticationUserQuery.findAndCheck(id);
        // 禁止锁定系统内置用户（系统管理员）
        if (Boolean.TRUE.equals(existing.getSysAdmin()) && Boolean.TRUE.equals(isLocked)) {
          throw ProtocolException.of("禁止锁定系统管理员「{0}」", new Object[]{existing.getName()});
        }
      }

      @Override
      protected User process() {
        existing.setLocked(isLocked);
        User saved = userRepo.save(existing);

        authUserDb.setAccountNonLocked(!isLocked);
        authenticationUserCmd.update0(authUserDb);

        // 记录操作日志
        if (Boolean.TRUE.equals(isLocked)) {
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.USER,
              saved.getId(),
              saved.getName(),
              OperationMessage.USER_LOCK_DETAILS,
              new Object[]{saved.getName()}
          );
        } else {
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.USER,
              saved.getId(),
              saved.getName(),
              OperationMessage.USER_UNLOCK_DETAILS,
              new Object[]{saved.getName()}
          );
        }

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      User existing;

      @Override
      protected void checkParams() {
        // 检查用户是否存在
        existing = userQuery.findAndCheck(id);

        // 禁止删除系统内置用户（系统管理员）
        if (Boolean.TRUE.equals(existing.getSysAdmin())) {
          throw ProtocolException.of("禁止删除系统管理员「{0}」", new Object[]{existing.getName()});
        }
      }

      @Override
      protected Void process() {
        String userName = existing.getName();

        // 先删除授权（必须在删除用户之前，否则可能因级联或 flush 顺序导致 gm_authorization 已被删除而触发 StaleStateException）
        authorizationCmd.deleteBySubjectTypeAndId(AuthorizationSubjectType.USER, id);
        authenticationUserCmd.deleteById(id);
        groupUserCmd.deleteByUserIds(List.of(id));
        departmentUserCmd.deleteByUserIds(List.of(id));
        userRepo.deleteById(id);

        // 减少用户配额使用量
        quotaManager.decreaseTenantQuota(
            tenantQuery.getMainTenantOfSameAccount(existing.getTenantId()).getId(),
            QuotaConstant.QuotaUserCount, 1L);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.USER,
            id,
            userName,
            OperationMessage.USER_DELETE_DETAILS,
            new Object[]{userName}
        );

        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void batchDelete(Set<Long> ids) {
    new BizTemplate<Void>() {
      List<User> usersToDelete;

      @Override
      protected void checkParams() {
        if (isEmpty(ids)) {
          return;
        }

        // 查询所有要删除的用户
        usersToDelete = userQuery.findAndCheck(ids);

        // 禁止删除系统内置用户（系统管理员）
        List<User> sysAdmins = usersToDelete.stream()
            .filter(user -> Boolean.TRUE.equals(user.getSysAdmin()))
            .toList();

        if (!sysAdmins.isEmpty()) {
          String adminNames = sysAdmins.stream()
              .map(User::getName)
              .collect(Collectors.joining("、"));
          throw ProtocolException.of("禁止删除系统管理员：{0}", new Object[]{adminNames});
        }
      }

      @Override
      protected Void process() {
        // 先删除授权（必须在删除用户之前，否则可能因级联或 flush 顺序导致 gm_authorization 已被删除而触发 StaleStateException）
        for (Long id : ids) {
          authorizationCmd.deleteBySubjectTypeAndId(AuthorizationSubjectType.USER, id);
          authenticationUserCmd.deleteById(id);
        }
        groupUserCmd.deleteByUserIds(ids);
        departmentUserCmd.deleteByUserIds(ids);
        userRepo.deleteAllById(ids);

        // 减少用户配额使用量（批量删除）
        quotaManager.decreaseTenantQuota(
            tenantQuery.getMainTenantOfSameAccount(get().getOptTenantId()).getId(),
            QuotaConstant.QuotaUserCount, (long) ids.size());

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.USER,
            null,
            "批量删除用户",
            OperationMessage.USER_BATCH_DELETE_DETAILS,
            new Object[]{ids.size()}
        );

        return null;
      }
    }.execute();
  }

  @Override
  public void update0(User user) {
    userRepo.save(user);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateMainDepartment(Long sourceDepartmentId, Long targetDepartmentId,
      List<Long> userIds) {
    if (isEmpty(userIds)) {
      return;
    }
    // 批量更新用户主部门：将主部门为源部门的用户更新为目标部门
    userRepo.updateMainDepartmentByIdIn(targetDepartmentId, userIds, sourceDepartmentId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateOfflineStatusByUsername(String principalName) {
    userRepo.updateOfflineStatusByUsername(principalName);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void clearMainDepartment(List<Long> userIds, Long departmentId) {
    if (isEmpty(userIds)) {
      return;
    }
    // 批量清除用户主部门：清除指定用户列表中主部门为指定部门的用户的主部门
    userRepo.clearMainDepartmentByIdIn(userIds, departmentId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void clearMainDepartment(Long departmentId) {
    // 批量清除用户主部门：清除指定部门下所有用户的主部门
    userRepo.clearMainDepartmentByDepartmentId(departmentId);
  }

  @Override
  public void deleteByTenantId(Long tenantId) {
    userRepo.deleteByTenantId(tenantId);
    authenticationUserCmd.deleteByTenantId(tenantId);
    quotaManager.resetTenantQuota(tenantQuery.getMainTenantOfSameAccount(tenantId).getId(),
        QuotaConstant.QuotaUserCount);
  }

  @Override
  protected BaseRepository<User, Long> getRepository() {
    return userRepo;
  }
}
