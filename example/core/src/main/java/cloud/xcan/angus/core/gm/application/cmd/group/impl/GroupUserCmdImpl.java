package cloud.xcan.angus.core.gm.application.cmd.group.impl;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.GroupUser;
import cloud.xcan.angus.api.commonlink.group.GroupUserRepo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.notification.NotificationHelperCmd;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.application.query.group.GroupUserQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.notification.NotificationMessage;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationPriority;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.ProtocolException;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupUserCmdImpl extends CommCmd<GroupUser, Long> implements GroupUserCmd {

  @Resource
  private GroupQuery groupQuery;

  @Resource
  private GroupUserQuery groupUserQuery;

  @Resource
  private GroupUserRepo groupUserRepo;

  @Resource
  private UserQuery userQuery;

  @Resource
  private NotificationHelperCmd notificationHelperCmd;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int addUsers(Long groupId, List<Long> userIds) {
    return new BizTemplate<Integer>() {
      Group group;

      @Override
      protected void checkParams() {
        // 检查组是否存在
        group = groupQuery.findAndCheck(groupId);

        // 检查用户是否存在
        userQuery.findAndCheck(userIds);
      }

      @Override
      protected Integer process() {
        // 获取组信息
        String groupName = group.getName();

        // 获取现有的组用户关系，避免重复
        List<GroupUser> existingRelations = groupUserRepo.findAllByGroupId(groupId);
        Set<Long> existingUserIds = existingRelations.stream()
            .map(GroupUser::getUserId)
            .collect(Collectors.toSet());

        // 过滤掉已经是成员的用户
        List<Long> newUserIds = userIds.stream()
            .filter(userId -> !existingUserIds.contains(userId))
            .toList();

        // 创建新的组用户关系
        if (!newUserIds.isEmpty()) {
          List<GroupUser> newRelations = new ArrayList<>();
          for (Long userId : newUserIds) {
            GroupUser groupUser = new GroupUser();
            groupUser.setGroupId(groupId);
            groupUser.setUserId(userId);
            newRelations.add(groupUser);
          }
          batchInsert0(newRelations);

          // 发送加入组通知给新加入的用户
          notificationHelperCmd.createBatchByMessageKey(
              NotificationType.SUCCESS,
              NotificationMessage.GROUP_MEMBER_ADDED_TITLE,
              NotificationMessage.GROUP_MEMBER_ADDED_DESCRIPTION,
              NotificationMessage.CATEGORY_GROUP_MANAGEMENT,
              NotificationPriority.MEDIUM,
              newUserIds,
              new Object[]{groupName},
              new Object[]{groupName}
          );

          // 记录操作日志
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.CREATE,
              ResourceType.ORGANIZATION,
              groupId,
              groupName,
              OperationMessage.GROUP_USER_ADD_DETAILS,
              new Object[]{groupName, newUserIds.size()}
          );
        }
        return newUserIds.size();
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int transferUsers(Long sourceGroupId, Long targetGroupId, List<Long> userIds) {
    return new BizTemplate<Integer>() {
      @Override
      protected void checkParams() {
        // 检查组是否存在
        groupQuery.findAndCheck(sourceGroupId);
        groupQuery.findAndCheck(targetGroupId);
      }

      @Override
      protected Integer process() {
        int successCount = 0;
        // 获取源组和目标组的现有关系
        List<GroupUser> sourceRelations = groupUserRepo.findAllByGroupId(sourceGroupId);
        List<GroupUser> targetRelations = groupUserRepo.findAllByGroupId(targetGroupId);

        Set<Long> sourceUserIds = sourceRelations.stream()
            .map(GroupUser::getUserId)
            .collect(Collectors.toSet());
        Set<Long> targetUserIds = targetRelations.stream()
            .map(GroupUser::getUserId)
            .collect(Collectors.toSet());

        for (Long userId : userIds) {
          // 检查源组关系是否存在
          if (sourceUserIds.contains(userId)) {
            // 查找源组关系
            GroupUser sourceRelation = sourceRelations.stream()
                .filter(gu -> gu.getUserId().equals(userId))
                .findFirst()
                .orElse(null);

            if (sourceRelation != null) {
              // 检查目标组关系是否已存在
              if (targetUserIds.contains(userId)) {
                // 如果已存在，只删除源组关系
                groupUserRepo.delete(sourceRelation);
              } else {
                // 如果不存在，创建新关系并删除旧关系
                GroupUser targetRelation = new GroupUser();
                targetRelation.setGroupId(targetGroupId);
                targetRelation.setUserId(userId);
                groupUserRepo.save(targetRelation);
                groupUserRepo.delete(sourceRelation);
                // 更新目标用户ID集合，避免重复创建
                targetUserIds.add(userId);
              }
              successCount++;
            }
          }
        }

        // 记录操作日志
        if (successCount > 0) {
          Group sourceGroup = groupQuery.findAndCheck(sourceGroupId);
          Group targetGroup = groupQuery.findAndCheck(targetGroupId);
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.ORGANIZATION,
              sourceGroupId,
              sourceGroup.getName(),
              OperationMessage.GROUP_USER_TRANSFER_DETAILS,
              new Object[]{sourceGroup.getName(), targetGroup.getName(), successCount}
          );
        }

        return successCount;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void removeUser(Long groupId, Long userId) {
    new BizTemplate<Void>() {
      Group group;
      User user;

      @Override
      protected void checkParams() {
        group = groupQuery.findAndCheck(groupId);
        user = userQuery.findAndCheck(userId);

        // 检查关系是否存在
        if (!groupUserQuery.existsByGroupIdAndUserId(groupId, userId)) {
          throw ProtocolException.of("用户「{0}」不属于该组「{0}」",
              new Object[]{user.getName(), group.getName()});
        }
      }

      @Override
      protected Void process() {
        // Delete group-user relation using repository method
        groupUserRepo.deleteByGroupIdAndUserId(groupId, List.of(userId));

        // 发送移除组通知
        notificationHelperCmd.createByMessageKey(
            NotificationType.WARNING,
            NotificationMessage.GROUP_MEMBER_REMOVED_TITLE,
            NotificationMessage.GROUP_MEMBER_REMOVED_DESCRIPTION,
            NotificationMessage.CATEGORY_GROUP_MANAGEMENT,
            NotificationPriority.MEDIUM,
            userId,
            new Object[]{group.getName()},
            new Object[]{group.getName()}
        );

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.ORGANIZATION,
            groupId,
            group.getName(),
            OperationMessage.GROUP_USER_REMOVE_DETAILS,
            new Object[]{group.getName(), user.getName()}
        );

        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void removeUsers(Long groupId, List<Long> userIds) {
    new BizTemplate<Void>() {
      Group group;

      @Override
      protected void checkParams() {
        group = groupQuery.findAndCheck(groupId);
      }

      @Override
      protected Void process() {
        // Delete group-user relations using repository method
        groupUserRepo.deleteByGroupIdAndUserId(groupId, userIds);

        // 发送移除组通知给被移除的用户
        if (!userIds.isEmpty()) {
          notificationHelperCmd.createBatchByMessageKey(
              NotificationType.WARNING,
              NotificationMessage.GROUP_MEMBER_REMOVED_TITLE,
              NotificationMessage.GROUP_MEMBER_REMOVED_DESCRIPTION,
              NotificationMessage.CATEGORY_GROUP_MANAGEMENT,
              NotificationPriority.MEDIUM,
              userIds,
              new Object[]{group.getName()},
              new Object[]{group.getName()}
          );

          // 记录操作日志
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.DELETE,
              ResourceType.ORGANIZATION,
              groupId,
              group.getName(),
              OperationMessage.GROUP_USER_REMOVE_BATCH_DETAILS,
              new Object[]{group.getName(), userIds.size()}
          );
        }
        return null;
      }
    }.execute();
  }

  @Override
  public void deleteByGroupId(Long groupId) {
    groupUserRepo.deleteAllByGroupIdIn(Set.of(groupId));
  }

  @Override
  public void deleteByUserIds(Collection<Long> ids) {
    groupUserRepo.deleteAllByUserIdIn(ids);
  }

  @Override
  protected BaseRepository<GroupUser, Long> getRepository() {
    return groupUserRepo;
  }
}
