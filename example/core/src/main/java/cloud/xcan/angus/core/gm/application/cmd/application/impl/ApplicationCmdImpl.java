package cloud.xcan.angus.core.gm.application.cmd.application.impl;

import static cloud.xcan.angus.spec.principal.PrincipalContext.get;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.QuotaConstant;
import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.commonlink.application.ApplicationRepo;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationSource;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.manager.QuotaManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.PermissionCheck;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.application.ApplicationCmd;
import cloud.xcan.angus.core.gm.application.cmd.application.ApplicationMenuCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.notification.NotificationHelperCmd;
import cloud.xcan.angus.core.gm.application.cmd.role.RoleCmd;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.notification.NotificationMessage;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationPriority;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationCmdImpl extends CommCmd<Application, Long> implements ApplicationCmd {

  @Resource
  private ApplicationRepo applicationRepo;

  @Resource
  private ApplicationQuery applicationQuery;

  @Resource
  private ApplicationMenuCmd applicationMenuCmd;

  @Resource
  private RoleCmd roleCmd;

  @Resource
  private ApplicationInfo applicationInfo;

  @Resource
  private NotificationHelperCmd notificationHelperCmd;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Resource
  private TenantQuery tenantQuery;

  @Resource
  private QuotaManager quotaManager;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Application create(Application application) {
    return new BizTemplate<Application>() {
      @Override
      protected void checkParams() {
        // 禁止创建云应用如果非运营租户
        PermissionCheck.checkCloudTenantSecurity();

        // 检查是否已存在相同code和version的应用
        applicationQuery.findByCodeAndEditionType(application.getCode(),
                applicationInfo.getEditionType())
            .ifPresent(existing -> {
              if (application.getVersion().equals(existing.getVersion())) {
                throw ResourceExisted.of("应用「{0}」版本「{1}」已存在",
                    new Object[]{application.getCode(), application.getVersion()});
              }
            });

        // 校验clientId是否存在
        if (application.getClientId() != null
            && applicationRepo.findByClientId(application.getClientId()).isEmpty()) {
          throw ResourceExisted.of("应用客户端ID「{0}」不存在",
              new Object[]{application.getClientId()});
        }
      }

      @Override
      protected Application process() {
        application.setEditionType(EditionType.valueOf(applicationInfo.getEditionType()));
        ApplicationSource source = nullSafe(application.getSource(), ApplicationSource.CUSTOM);
        application.setSource(source);
        insert(application);

        // 如果创建的是自定义应用，增加配额使用量
        if (ApplicationSource.CUSTOM.equals(source)) {
          quotaManager.increaseTenantQuota(
              tenantQuery.getMainTenantOfSameAccount(get().getOptTenantId()).getId(),
              QuotaConstant.QuotaCustomApplications, 1L);
        }

        // 记录操作日志
        String appName = application.getName() != null
            ? application.getName() : application.getCode();
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.APPLICATION,
            application.getId(),
            appName,
            OperationMessage.APPLICATION_CREATE_DETAILS,
            new Object[]{appName}
        );

        return application;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Application update(Application application) {
    return new BizTemplate<Application>() {
      Application existing;

      @Override
      protected void checkParams() {
        // 如果非运营租户，禁止修改来源是安装类型的应用和云应用
        existing = applicationQuery.checkCanModify(application.getId());

        // 如果source是INSTALLED，验证应用和版本是否存在
        if (application.getCode() != null && application.getVersion() != null) {
          // 检查是否已存在相同code和version的其他应用
          applicationQuery.findByCodeAndEditionType(
              application.getCode(), applicationInfo.getEditionType()).ifPresent(app -> {
            if (!app.getId().equals(application.getId())
                && application.getVersion().equals(app.getVersion())) {
              throw ResourceExisted.of("应用「{0}」版本「{1}」已存在",
                  new Object[]{application.getCode(), application.getVersion()});
            }
          });
        }

        // 校验clientId是否存在
        if (application.getClientId() != null
            && applicationRepo.findByClientId(application.getClientId()).isEmpty()) {
          throw ResourceExisted.of("应用客户端ID「{0}」不存在",
              new Object[]{application.getClientId()});
        }
      }

      @Override
      protected Application process() {
        CoreUtils.copyPropertiesIgnoreNull(application, existing);
        applicationRepo.save(existing);

        // 记录操作日志
        String appName = existing.getName() != null ? existing.getName() : existing.getCode();
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.APPLICATION,
            existing.getId(),
            appName,
            OperationMessage.APPLICATION_UPDATE_DETAILS,
            new Object[]{appName}
        );
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Application updateStatus(Long id, EnabledStatus status) {
    return new BizTemplate<Application>() {
      Application existing;

      @Override
      protected void checkParams() {
        // 如果非运营租户，禁止修改来源是安装类型的应用和云应用
        existing = applicationQuery.checkCanModify(id);

        // 安装应用不允许删除
        if (ApplicationSource.INSTALLED.equals(existing.getSource())) {
          throw ProtocolException.of("安装应用「{0}」不允许修改状态",
              new Object[]{existing.getCode()});
        }
      }

      @Override
      protected Application process() {
        // 更新应用状态
        EnabledStatus oldStatus = existing.getStatus();
        existing.setStatus(status);
        applicationRepo.save(existing);

        // 发送通知
        String appName = existing.getName() != null ? existing.getName() : existing.getCode();
        Long currentUserId = getUserId();
        Long createdBy = existing.getCreatedBy();

        if (EnabledStatus.DISABLED.equals(status) && !EnabledStatus.DISABLED.equals(oldStatus)) {
          // 应用被禁用
          // 如果当前用户和创建用户不同，给创建用户发送通知
          if (createdBy != null && !createdBy.equals(currentUserId)) {
            notificationHelperCmd.createByMessageKey(
                NotificationType.WARNING,
                NotificationMessage.APPLICATION_DISABLED_TITLE,
                NotificationMessage.APPLICATION_DISABLED_DESCRIPTION,
                NotificationMessage.CATEGORY_APPLICATION_MANAGEMENT,
                NotificationPriority.MEDIUM,
                createdBy,
                new Object[]{appName},
                new Object[]{appName}
            );
          }
          // 记录操作日志
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.APPLICATION,
              existing.getId(),
              appName,
              OperationMessage.APPLICATION_DISABLE_DETAILS,
              new Object[]{appName}
          );
        } else if (EnabledStatus.ENABLED.equals(status)
            && !EnabledStatus.ENABLED.equals(oldStatus)) {
          // 应用被启用
          // 如果当前用户和创建用户不同，给创建用户发送通知
          if (createdBy != null && !createdBy.equals(currentUserId)) {
            notificationHelperCmd.createByMessageKey(
                NotificationType.SUCCESS,
                NotificationMessage.APPLICATION_ENABLED_TITLE,
                NotificationMessage.APPLICATION_ENABLED_DESCRIPTION,
                NotificationMessage.CATEGORY_APPLICATION_MANAGEMENT,
                NotificationPriority.MEDIUM,
                createdBy,
                new Object[]{appName},
                new Object[]{appName}
            );
          }
          // 记录操作日志
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.APPLICATION,
              existing.getId(),
              appName,
              OperationMessage.APPLICATION_ENABLE_DETAILS,
              new Object[]{appName}
          );
        }

        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      Application existing;

      @Override
      protected void checkParams() {
        // 如果非运营租户，禁止修改来源是安装类型的应用和云应用
        existing = applicationQuery.checkCanModify(id);

        // 安装应用不允许删除
        if (ApplicationSource.INSTALLED.equals(existing.getSource())) {
          throw ProtocolException.of("安装应用「{0}」不允许删除", new Object[]{existing.getCode()});
        }
      }

      @Override
      protected Void process() {
        // 保存应用名称和创建用户用于通知（删除前获取）
        String appName = existing.getName() != null ? existing.getName() : existing.getCode();
        Long currentUserId = getUserId();
        Long createdBy = existing.getCreatedBy();
        ApplicationSource source = existing.getSource();

        applicationRepo.deleteById(id);
        applicationMenuCmd.deleteByApplicationId(id);
        roleCmd.deleteByApplicationId(id);

        // 如果删除的是自定义应用，减少配额使用量
        if (ApplicationSource.CUSTOM.equals(source)) {
          quotaManager.decreaseTenantQuota(
              tenantQuery.getMainTenantOfSameAccount(get().getOptTenantId()).getId(),
              QuotaConstant.QuotaCustomApplications, 1L);
        }

        // 发送通知
        // 如果当前用户和创建用户不同，给创建用户发送通知
        if (createdBy != null && !createdBy.equals(currentUserId)) {
          notificationHelperCmd.createByMessageKey(
              NotificationType.WARNING,
              NotificationMessage.APPLICATION_DELETED_TITLE,
              NotificationMessage.APPLICATION_DELETED_DESCRIPTION,
              NotificationMessage.CATEGORY_APPLICATION_MANAGEMENT,
              NotificationPriority.HIGH,
              createdBy,
              new Object[]{appName},
              new Object[]{appName}
          );
        }

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.APPLICATION,
            id,
            appName,
            OperationMessage.APPLICATION_DELETE_DETAILS,
            new Object[]{appName}
        );

        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Application, Long> getRepository() {
    return applicationRepo;
  }
}
