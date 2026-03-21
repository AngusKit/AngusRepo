package cloud.xcan.angus.core.gm.application.cmd.security.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getApplicationInfo;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.PermissionCheck;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.authentication.AuthenticationClientCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.security.SecurityCmd;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.security.Security;
import cloud.xcan.angus.core.gm.domain.security.SecurityRepo;
import cloud.xcan.angus.core.gm.domain.security.SecurityType;
import cloud.xcan.angus.core.gm.domain.security.model.IpWhitelistConfig;
import cloud.xcan.angus.core.gm.domain.security.model.LoginSecurityConfig;
import cloud.xcan.angus.core.gm.domain.security.model.PasswordPolicyConfig;
import cloud.xcan.angus.core.gm.domain.security.model.SecurityNotificationConfig;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SecurityCmdImpl extends CommCmd<Security, Long> implements SecurityCmd {

  @Resource
  private SecurityRepo securityRepo;

  @Resource
  private AuthenticationClientCmd authenticationClientCmd;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Security updatePasswordPolicy(PasswordPolicyConfig config) {
    return new BizTemplate<Security>() {
      Security existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();
        existing = securityRepo
            .findFirstByType(SecurityType.PASSWORD_POLICY)
            .orElse(null);
      }

      @Override
      protected Security process() {
        if (existing == null) {
          existing = new Security();
          existing.setId(uidGenerator.getUID());
          existing.setName("密码策略");
          existing.setType(SecurityType.PASSWORD_POLICY);
        }

        // Hibernate JsonType 会自动序列化
        existing.setConfig(config);
        existing.setStatus(EnabledStatus.ENABLED);

        Security saved = securityRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            saved.getId(),
            saved.getName(),
            OperationMessage.SECURITY_UPDATE_PASSWORD_POLICY_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Security updateLoginSecurityConfig(LoginSecurityConfig config) {
    return new BizTemplate<Security>() {
      Security existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();
        // 仅云服务版和数据中心版可开启允许注册
        if (Boolean.TRUE.equals(config.getAllowRegistrationEnabled())) {
          String editionType = getApplicationInfo().getEditionType();
          boolean isAllowed = EditionType.CLOUD_SERVICE.name().equals(editionType)
              || EditionType.DATACENTER.name().equals(editionType);
          if (!isAllowed) {
            throw ProtocolException.of("允许注册新账号仅支持云服务版和数据中心版，当前版本不可开启");
          }
        }
        existing = securityRepo
            .findFirstByType(SecurityType.LOGIN_SECURITY)
            .orElse(null);
      }

      @Override
      protected Security process() {
        if (existing == null) {
          existing = new Security();
          existing.setId(uidGenerator.getUID());
          existing.setName("登录安全");
          existing.setType(SecurityType.LOGIN_SECURITY);
        }

        if (existing.getConfig() == null) {
          existing.setConfig(new LoginSecurityConfig());
        }

        // 如果修改了 sessionTimeoutMinutes，修改OAuth Client访问令牌有效期
        LoginSecurityConfig securityConfigDb = (LoginSecurityConfig) existing.getConfig();
        if (!securityConfigDb.getSessionTimeoutMinutes()
            .equals(config.getSessionTimeoutMinutes())) {
          authenticationClientCmd.updateAccessTokenTimeToLive(
              config.getSessionTimeoutMinutes() * 60);
        }

        // Hibernate JsonType 会自动序列化
        existing.setConfig(config);
        existing.setStatus(EnabledStatus.ENABLED);
        Security saved = securityRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            saved.getId(),
            saved.getName(),
            OperationMessage.SECURITY_UPDATE_LOGIN_SECURITY_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Security addIpWhitelist(IpWhitelistConfig config) {
    return new BizTemplate<Security>() {
      Security existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();
        existing = securityRepo
            .findFirstByType(SecurityType.IP_WHITELIST)
            .orElse(null);
      }

      @Override
      protected Security process() {
        if (existing == null) {
          existing = new Security();
          existing.setId(uidGenerator.getUID());
          existing.setName("IP白名单");
          existing.setType(SecurityType.IP_WHITELIST);
        }

        // Hibernate JsonType 会自动序列化
        existing.setConfig(config);
        existing.setStatus(config.getStatus());

        Security saved = securityRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.CONFIG,
            saved.getId(),
            saved.getName(),
            OperationMessage.SECURITY_ADD_IP_WHITELIST_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Security updateIpWhitelist(Long id, IpWhitelistConfig config) {
    return new BizTemplate<Security>() {
      Security existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();

        existing = securityRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("IP白名单未找到", new Object[]{}));
      }

      @Override
      protected Security process() {
        // Hibernate JsonType 会自动序列化
        existing.setConfig(config);
        existing.setStatus(config.getStatus());

        Security saved = securityRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            saved.getId(),
            saved.getName(),
            OperationMessage.SECURITY_UPDATE_IP_WHITELIST_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteIpWhitelist(Long id) {
    new BizTemplate<Void>() {
      Security security;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();

        security = securityRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("IP白名单未找到", new Object[]{}));
      }

      @Override
      protected Void process() {
        String securityName = security.getName();

        securityRepo.deleteById(id);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.CONFIG,
            id,
            securityName,
            OperationMessage.SECURITY_DELETE_IP_WHITELIST_DETAILS,
            new Object[]{securityName}
        );
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Security updateNotificationConfig(SecurityNotificationConfig config) {
    return new BizTemplate<Security>() {
      Security existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();
        existing = securityRepo
            .findFirstByType(SecurityType.NOTIFICATION_CONFIG)
            .orElse(null);
      }

      @Override
      protected Security process() {
        if (existing == null) {
          existing = new Security();
          existing.setId(uidGenerator.getUID());
          existing.setName("安全通知");
          existing.setType(SecurityType.NOTIFICATION_CONFIG);
        }

        existing.setConfig(config);
        existing.setStatus(EnabledStatus.ENABLED);

        Security saved = securityRepo.save(existing);

        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            saved.getId(),
            saved.getName(),
            OperationMessage.SECURITY_UPDATE_NOTIFICATION_CONFIG_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Security, Long> getRepository() {
    return securityRepo;
  }
}
