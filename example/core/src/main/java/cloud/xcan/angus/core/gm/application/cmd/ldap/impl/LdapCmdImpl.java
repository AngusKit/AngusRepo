package cloud.xcan.angus.core.gm.application.cmd.ldap.impl;

import static cloud.xcan.angus.core.gm.application.converter.LdapConverter.createSyncHistory;
import static cloud.xcan.angus.core.gm.application.converter.LdapConverter.createUserFromLdap;
import static cloud.xcan.angus.core.gm.application.converter.LdapConverter.getSearchAttributes;
import static cloud.xcan.angus.core.gm.application.converter.LdapConverter.toHistory;
import static cloud.xcan.angus.core.gm.application.converter.LdapConverter.updateUserFromLdap;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.commonlink.user.enums.UserSource;
import cloud.xcan.angus.api.commonlink.user.enums.UserStatus;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.PermissionCheck;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.ldap.LdapCmd;
import cloud.xcan.angus.core.gm.application.cmd.ldap.LdapSyncHistoryCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.query.ldap.LdapQuery;
import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.domain.ldap.LdapRepo;
import cloud.xcan.angus.core.gm.domain.ldap.LdapSyncHistory;
import cloud.xcan.angus.core.gm.domain.ldap.LdapSyncHistoryRepo;
import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapSyncStatus;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.infra.ldap.LdapClientService;
import cloud.xcan.angus.core.gm.infra.ldap.LdapClientServiceFactory;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * LDAP命令服务实现
 */
@Service
public class LdapCmdImpl extends CommCmd<Ldap, Long> implements LdapCmd {

  @Resource
  private LdapRepo ldapRepo;

  @Resource
  private LdapQuery ldapQuery;

  @Resource
  private UserCmd userCmd;

  @Resource
  private UserRepo userRepo;

  @Resource
  private LdapSyncHistoryCmd ldapSyncHistoryCmd;

  @Resource
  private LdapSyncHistoryRepo ldapSyncHistoryRepo;

  @Resource
  private LdapClientServiceFactory ldapClientServiceFactory;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Ldap create(Ldap ldap) {
    return new BizTemplate<Ldap>(false) {
      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();

        // 检查名称是否已存在
        if (ldap.getName() != null) {
          Optional<Ldap> existing = ldapQuery.findByName(ldap.getName());
          if (existing.isPresent()) {
            throw ResourceExisted.of("LDAP配置名称「{0}」已存在", new Object[]{ldap.getName()});
          }
        }
      }

      @Override
      protected Ldap process() {
        Ldap saved = insert(ldap);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.CONFIG,
            saved.getId(),
            saved.getName(),
            OperationMessage.LDAP_CREATE_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>(false) {
      Ldap existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();

        // 检查LDAP配置是否存在
        existing = ldapQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        // 保存LDAP配置名称用于操作日志（删除前获取）
        String ldapName = existing.getName();

        // 删除相关的同步历史记录
        List<LdapSyncHistory> syncHistories = ldapSyncHistoryRepo.findByLdapId(id);
        if (syncHistories != null && !syncHistories.isEmpty()) {
          ldapSyncHistoryRepo.deleteAll(syncHistories);
        }

        // 删除LDAP配置
        ldapRepo.deleteById(id);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.CONFIG,
            id,
            ldapName,
            OperationMessage.LDAP_DELETE_DETAILS,
            new Object[]{ldapName}
        );

        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Ldap updateConfig(Long id, Ldap ldap) {
    return new BizTemplate<Ldap>(false) {
      Ldap existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();
        existing = ldapQuery.findAndCheck(id);
      }

      @Override
      protected Ldap process() {
        // 更新配置字段（不更新启用状态，使用 updateStatus 接口）
        CoreUtils.copyPropertiesIgnoreNull(ldap, existing);
        // 保存配置
        Ldap saved = ldapRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            saved.getId(),
            saved.getName(),
            OperationMessage.LDAP_UPDATE_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Ldap updateStatus(Long id, EnabledStatus status) {
    return new BizTemplate<Ldap>(false) {
      Ldap existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();
        existing = ldapQuery.findAndCheck(id);
      }

      @Override
      protected Ldap process() {
        existing.setEnabled(status.isEnabled());
        Ldap saved = ldapRepo.save(existing);

        // 记录操作日志
        if (EnabledStatus.ENABLED.equals(status)) {
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.CONFIG,
              id,
              existing.getName(),
              OperationMessage.LDAP_UPDATE_DETAILS,
              new Object[]{existing.getName()}
          );
        } else {
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.CONFIG,
              id,
              existing.getName(),
              OperationMessage.LDAP_UPDATE_DETAILS,
              new Object[]{existing.getName()}
          );
        }

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public LdapSyncHistory syncUsers(Ldap config, LdapSyncHistory history, boolean testMode) {
    return new BizTemplate<LdapSyncHistory>(false) {
      LdapClientService clientService;
      Map<String, String> fieldMapping;
      List<LdapClientService.LdapEntry> ldapUsers;
      LocalDateTime startTime;
      int totalUsers = 0;
      int newUsers = 0;
      int updatedUsers = 0;
      int deletedUsers = 0;
      int failedUsers = 0;
      String errorMessage = null;

      @Override
      protected void checkParams() {
        if (isUserAction()) {
          PermissionCheck.checkCloudTenantSecurity();
        }
        if (!config.getEnabled()) {
          throw ProtocolException.of("LDAP配置未启用");
        }
        if (history == null) {
          throw ProtocolException.of("同步历史记录不能为空");
        }
      }

      @Override
      protected LdapSyncHistory process() {
        startTime = LocalDateTime.now();
        history.setId(uidGenerator.getUID());
        history.setStatus(LdapSyncStatus.RUNNING);
        history.setStartTime(startTime);

        try {
          // 1. 创建LDAP客户端服务
          clientService = ldapClientServiceFactory.create(config);

          // 2. 获取字段映射配置
          fieldMapping = ldapQuery.getFieldMapping(config.getId());

          // 3. 从LDAP服务器获取所有用户
          String[] attributes = getSearchAttributes(fieldMapping);
          ldapUsers = clientService.searchUsers(config.getBaseDn(), config.getUserFilter(),
              attributes, null);
          totalUsers = ldapUsers.size();

          // 4. 获取所有现有用户（用于删除检测）
          List<User> existingUsers = userRepo.findAll();

          // 5. 处理每个LDAP用户
          Map<String, User> matchedUsers = new HashMap<>();
          for (LdapClientService.LdapEntry ldapEntry : ldapUsers) {
            try {
              User user = processLdapUser(ldapEntry, fieldMapping, testMode);
              if (user != null && user.getId() != null) {
                matchedUsers.put(user.getUsername(), user);
              }
            } catch (Exception e) {
              failedUsers++;
              if (errorMessage == null) {
                errorMessage = "同步用户失败: " + e.getMessage();
              }
            }
          }

          // 6. 检测并处理从LDAP删除的用户：对 source==LDAP_SYNC 且 ldapId==当前配置 的用户做禁用
          if (!testMode) {
            Long configId = config.getId();
            for (User existingUser : existingUsers) {
              if (!matchedUsers.containsKey(existingUser.getUsername())
                  && UserSource.LDAP_SYNC.equals(existingUser.getSource())
                  && configId != null && configId.equals(existingUser.getLdapId())) {
                existingUser.setStatus(UserStatus.DISABLED);
                userCmd.update(existingUser);
                deletedUsers++;
              }
            }
          }

          // 7. 更新同步历史记录
          toHistory(history,
              failedUsers > 0 ? LdapSyncStatus.FAILED : LdapSyncStatus.SUCCESS,
              errorMessage, startTime, totalUsers, newUsers, updatedUsers, deletedUsers,
              failedUsers);

          // 8. 保存同步历史记录（测试模式下也保存历史记录）
          LdapSyncHistory savedHistory;
          if (history.getId() == null) {
            savedHistory = ldapSyncHistoryCmd.create(history);
          } else {
            savedHistory = ldapSyncHistoryRepo.save(history);
          }

          // 记录操作日志
          String testModeText = testMode ? "YES" : "NO";
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.CONFIG,
              config.getId(),
              config.getName(),
              OperationMessage.LDAP_SYNC_USERS_DETAILS,
              new Object[]{config.getName(), totalUsers, newUsers, updatedUsers, testModeText}
          );

          return savedHistory;

        } catch (Exception e) {
          // 同步失败
          toHistory(history, LdapSyncStatus.FAILED, e.getMessage(),
              startTime, totalUsers, newUsers, updatedUsers, deletedUsers, failedUsers);
          LdapSyncHistory savedHistory;
          if (history.getId() == null) {
            savedHistory = ldapSyncHistoryCmd.create(history);
          } else {
            savedHistory = ldapSyncHistoryRepo.save(history);
          }

          // 记录操作日志（失败时也记录）
          String testModeText = testMode ? "YES" : "NO";
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.CONFIG,
              config.getId(),
              config.getName(),
              OperationMessage.LDAP_SYNC_USERS_DETAILS,
              new Object[]{config.getName(), totalUsers, newUsers, updatedUsers, testModeText}
          );

          return savedHistory;
        }
      }

      /**
       * 处理单个LDAP用户
       */
      private User processLdapUser(LdapClientService.LdapEntry ldapEntry,
          Map<String, String> fieldMapping, boolean testMode) {
        // 1. 提取LDAP用户属性
        String uidField = fieldMapping != null
            ? fieldMapping.getOrDefault("uid", "uid") : "uid";
        String cnField = fieldMapping != null
            ? fieldMapping.getOrDefault("cn", "cn") : "cn";
        String mailField = fieldMapping != null
            ? fieldMapping.getOrDefault("mail", "mail") : "mail";
        String departmentField = fieldMapping != null
            ? fieldMapping.getOrDefault("department", "department") : "department";
        String titleField = fieldMapping != null
            ? fieldMapping.getOrDefault("title", "title") : "title";
        String mobileField = fieldMapping != null
            ? fieldMapping.getOrDefault("mobile", "mobile") : "mobile";

        String username = ldapEntry.getAttributeValue(uidField);
        String name = ldapEntry.getAttributeValue(cnField);
        String email = ldapEntry.getAttributeValue(mailField);
        //String department = ldapEntry.getAttributeValue(departmentField);
        String jobTitle = ldapEntry.getAttributeValue(titleField);
        String phone = ldapEntry.getAttributeValue(mobileField);

        if (username == null || username.isEmpty()) {
          throw ProtocolException.of("LDAP用户缺少用户名(uid)属性");
        }

        // 2. 查找现有用户（优先通过username，其次通过email）
        User existingUser = userRepo.findByUsername(username);
        if (existingUser == null && email != null && !email.isEmpty()) {
          existingUser = userRepo.findByEmail(email);
        }

        // 3. 创建或更新用户
        if (existingUser == null) {
          // 创建新用户
          if (!testMode) {
            User newUser = createUserFromLdap(username, name, email, phone, jobTitle, config);
            newUsers++;
            return userCmd.create(newUser);
          } else {
            // 测试模式：只计数，不创建
            newUsers++;
            return null;
          }
        } else {
          // 更新现有用户
          if (!testMode) {
            updateUserFromLdap(existingUser, name, email, phone, jobTitle);
            updatedUsers++;
            return userCmd.update(existingUser);
          } else {
            // 测试模式：只计数，不更新
            updatedUsers++;
            return existingUser;
          }
        }
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void syncAllEnabledUsers() {
    List<Ldap> enabledConfigs = ldapQuery.findByEnabled(true);
    if (enabledConfigs == null || enabledConfigs.isEmpty()) {
      return;
    }
    for (Ldap config : enabledConfigs) {
      LdapSyncHistory history = createSyncHistory(config);
      syncUsers(config, history, false);
    }
  }

  @Override
  protected BaseRepository<Ldap, Long> getRepository() {
    return ldapRepo;
  }
}
