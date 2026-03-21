package cloud.xcan.angus.core.gm.application.cmd.group.impl;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.QuotaConstant;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.GroupRepo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.manager.QuotaManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.authorization.AuthorizationCmd;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupCmd;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.notification.NotificationHelperCmd;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.application.query.group.GroupUserQuery;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.notification.NotificationMessage;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationPriority;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupCmdImpl extends CommCmd<Group, Long> implements GroupCmd {

  @Resource
  private GroupRepo groupRepo;

  @Resource
  private GroupQuery groupQuery;

  @Resource
  private GroupUserCmd groupUserCmd;

  @Resource
  private GroupUserQuery groupUserQuery;

  @Resource
  private UserQuery userQuery;

  @Resource
  private AuthorizationCmd authorizationCmd;

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
  public Group create(Group group) {
    return new BizTemplate<Group>() {
      @Override
      protected void checkParams() {
        // 检查组名称是否存在
        if (groupRepo.existsByName(group.getName())) {
          throw ResourceExisted.of("组名称「{0}」已存在",
              new Object[]{group.getName()});
        }

        // 检查组编码是否存在
        if (groupRepo.existsByCode(group.getCode())) {
          throw ResourceExisted.of("组编码「{0}」已存在",
              new Object[]{group.getCode()});
        }
      }

      @Override
      protected Group process() {
        insert(group);

        // 增加组配额使用量
        quotaManager.increaseTenantQuota(
            tenantQuery.getMainTenantOfSameAccount(group.getTenantId()).getId(),
            QuotaConstant.QuotaGroupCount, 1L);

        // 如果指定了组成员，保存组和用户关系记录
        if (group.getUserIds() != null && !group.getUserIds().isEmpty()) {
          groupUserCmd.addUsers(group.getId(), group.getUserIds());
        }

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.ORGANIZATION,
            group.getId(),
            group.getName(),
            OperationMessage.GROUP_CREATE_DETAILS,
            new Object[]{group.getName()}
        );
        return group;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Group update(Group group) {
    return new BizTemplate<Group>() {
      Group existing;

      @Override
      protected void checkParams() {
        // 检查组是否存在
        existing = groupQuery.findAndCheck(group.getId());

        // 检查组名称是否存在
        if (groupRepo.existsByNameAndIdNot(group.getName(), group.getId())) {
          throw ResourceExisted.of("组名称「{0}」已存在",
              new Object[]{group.getName()});
        }

        // 检查组编码是否存在
        if (groupRepo.existsByCodeAndIdNot(group.getCode(), group.getId())) {
          throw ResourceExisted.of("组编码「{0}」已存在",
              new Object[]{group.getCode()});
        }
      }

      @Override
      protected Group process() {
        CoreUtils.copyPropertiesIgnoreNull(group, existing);
        groupRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.ORGANIZATION,
            group.getId(),
            existing.getName(),
            OperationMessage.GROUP_UPDATE_DETAILS,
            new Object[]{existing.getName()}
        );

        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Group updateStatus(Long id, EnabledStatus status) {
    return new BizTemplate<Group>() {
      Group groupDb;

      @Override
      protected void checkParams() {
        groupDb = groupQuery.findAndCheck(id);
      }

      @Override
      protected Group process() {
        groupDb.setStatus(status);
        groupRepo.save(groupDb);

        // 记录操作日志
        if (EnabledStatus.ENABLED.equals(status)) {
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.ORGANIZATION,
              id,
              groupDb.getName(),
              OperationMessage.GROUP_ENABLE_DETAILS,
              new Object[]{groupDb.getName()}
          );
        } else if (EnabledStatus.DISABLED.equals(status)) {
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.ORGANIZATION,
              id,
              groupDb.getName(),
              OperationMessage.GROUP_DISABLE_DETAILS,
              new Object[]{groupDb.getName()}
          );
        }

        return groupDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Group updateOwner(Long groupId, Long ownerId) {
    return new BizTemplate<Group>() {
      Group groupDb;
      User owner;

      @Override
      protected void checkParams() {
        groupDb = groupQuery.findAndCheck(groupId);
        owner = userQuery.findAndCheck(ownerId);
      }

      @Override
      protected Group process() {
        groupDb.setOwnerId(ownerId);
        groupRepo.save(groupDb);

        // 记录操作日志
        String ownerName = owner.getName();
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.ORGANIZATION,
            groupId,
            groupDb.getName(),
            OperationMessage.GROUP_UPDATE_OWNER_DETAILS,
            new Object[]{groupDb.getName(), ownerName}
        );

        return groupDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      Group group;
      List<Long> userIds;

      @Override
      protected void checkParams() {
        group = groupQuery.findAndCheck(id);
        // 获取组下的所有用户ID
        userIds = groupUserQuery.findUserIdsByGroupId(id);
      }

      @Override
      protected Void process() {
        // 减少组配额使用量
        quotaManager.decreaseTenantQuota(
            tenantQuery.getMainTenantOfSameAccount(group.getTenantId()).getId(),
            QuotaConstant.QuotaGroupCount, 1L);

        // 删除组与用户关系
        groupUserCmd.deleteByGroupId(id);
        // 删除组
        groupRepo.deleteById(id);
        // 删除组授权
        authorizationCmd.deleteBySubjectTypeAndId(AuthorizationSubjectType.GROUP, id);

        // 发送组解散通知给组下所有用户
        if (!userIds.isEmpty()) {
          notificationHelperCmd.createBatchByMessageKey(
              NotificationType.WARNING,
              NotificationMessage.GROUP_DELETED_TITLE,
              NotificationMessage.GROUP_DELETED_DESCRIPTION,
              NotificationMessage.CATEGORY_GROUP_MANAGEMENT,
              NotificationPriority.HIGH,
              userIds,
              new Object[]{group.getName()},
              new Object[]{group.getName()}
          );
        }

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.ORGANIZATION,
            id,
            group.getName(),
            OperationMessage.GROUP_DELETE_DETAILS,
            new Object[]{group.getName()}
        );

        return null;
      }
    }.execute();
  }

  @Override
  public void deleteByTenantId(Long tenantId) {
    groupRepo.deleteByTenantId(tenantId);
    quotaManager.resetTenantQuota(tenantQuery.getMainTenantOfSameAccount(tenantId).getId(),
        QuotaConstant.QuotaGroupCount);
  }

  @Override
  protected BaseRepository<Group, Long> getRepository() {
    return groupRepo;
  }
}
