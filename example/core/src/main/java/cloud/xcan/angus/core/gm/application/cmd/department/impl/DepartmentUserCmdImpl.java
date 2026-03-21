package cloud.xcan.angus.core.gm.application.cmd.department.impl;

import cloud.xcan.angus.api.commonlink.department.Department;
import cloud.xcan.angus.api.commonlink.department.DepartmentUser;
import cloud.xcan.angus.api.commonlink.department.DepartmentUserRepo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.department.DepartmentUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.notification.NotificationHelperCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentQuery;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentUserQuery;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentUserCmdImpl extends CommCmd<DepartmentUser, Long>
    implements DepartmentUserCmd {

  @Resource
  private DepartmentUserRepo departmentUserRepo;

  @Resource
  private DepartmentUserQuery departmentUserQuery;

  @Resource
  private DepartmentQuery departmentQuery;

  @Resource
  private UserCmd userCmd;

  @Resource
  private UserQuery userQuery;

  @Resource
  private NotificationHelperCmd notificationHelperCmd;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int addUsers(Long departmentId, List<Long> userIds) {
    return new BizTemplate<Integer>() {
      Department department;

      @Override
      protected void checkParams() {
        // 检查部门是否存在
        department = departmentQuery.findAndCheck(departmentId);

        // 检查用户是否存在
        userQuery.findAndCheck(userIds);
      }

      @Override
      protected Integer process() {
        // 获取部门信息
        String departmentName = department.getName();

        // 获取现有的部门用户关系，避免重复
        List<DepartmentUser> existingRelations
            = departmentUserQuery.findByDepartmentId(departmentId);
        Set<Long> existingUserIds = existingRelations.stream()
            .map(DepartmentUser::getUserId)
            .collect(Collectors.toSet());

        // 过滤掉已经是成员的用户
        List<Long> newUserIds = userIds.stream()
            .filter(userId -> !existingUserIds.contains(userId))
            .toList();

        // 创建新的部门用户关系
        if (!newUserIds.isEmpty()) {
          // 批量查询所有新用户的部门关系，避免在循环中调用数据库
          List<DepartmentUser> allUserDepartments
              = departmentUserRepo.findAllByUserIdIn(newUserIds);

          // 按用户ID分组，便于快速查找
          Map<Long, List<DepartmentUser>> userDepartmentsMap = allUserDepartments.stream()
              .collect(Collectors.groupingBy(DepartmentUser::getUserId));

          List<DepartmentUser> newRelations = new ArrayList<>();
          for (Long userId : newUserIds) {
            // 从内存中获取用户当前的部门关系
            List<DepartmentUser> userDepartments = userDepartmentsMap.get(userId);
            boolean isPrimary = (userDepartments == null || userDepartments.isEmpty());

            DepartmentUser departmentUser = new DepartmentUser();
            departmentUser.setDepartmentId(departmentId);
            departmentUser.setUserId(userId);
            departmentUser.setIsPrimary(isPrimary);
            departmentUser.setIsManager(false);
            departmentUser.setTenantId(department.getTenantId());
            newRelations.add(departmentUser);
          }
          batchInsert0(newRelations);

          // 发送加入部门通知给新加入的用户
          notificationHelperCmd.createBatchByMessageKey(
              NotificationType.SUCCESS,
              NotificationMessage.DEPARTMENT_MEMBER_ADDED_TITLE,
              NotificationMessage.DEPARTMENT_MEMBER_ADDED_DESCRIPTION,
              NotificationMessage.CATEGORY_DEPARTMENT_MANAGEMENT,
              NotificationPriority.MEDIUM,
              newUserIds,
              new Object[]{departmentName},
              new Object[]{departmentName}
          );

          // 记录操作日志
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.CREATE,
              ResourceType.ORGANIZATION,
              departmentId,
              departmentName,
              OperationMessage.DEPARTMENT_USER_ADD_DETAILS,
              new Object[]{departmentName, newUserIds.size()}
          );
        }
        return newUserIds.size();
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int transferUsers(Long sourceDepartmentId, Long targetDepartmentId, List<Long> userIds) {
    return new BizTemplate<Integer>() {
      @Override
      protected void checkParams() {
        // 检查部门是否存在
        departmentQuery.findAndCheck(sourceDepartmentId);
        departmentQuery.findAndCheck(targetDepartmentId);
      }

      @Override
      protected Integer process() {
        int successCount = 0;
        for (Long userId : userIds) {
          // 检查源部门关系是否存在
          var sourceUserOpt = departmentUserQuery.findByDepartmentIdAndUserId(
              sourceDepartmentId, userId);
          if (sourceUserOpt.isPresent()) {
            DepartmentUser sourceUser = sourceUserOpt.get();

            // 检查目标部门关系是否已存在
            var targetUserOpt = departmentUserQuery.findByDepartmentIdAndUserId(
                targetDepartmentId, userId);
            if (targetUserOpt.isPresent()) {
              // 如果已存在，删除源部门关系
              departmentUserRepo.delete(sourceUser);
              // 保留原部门是否主部门关系
              if (!sourceUser.getIsPrimary().equals(targetUserOpt.get().getIsPrimary())) {
                targetUserOpt.get().setIsPrimary(sourceUser.getIsPrimary());
                departmentUserRepo.save(targetUserOpt.get());
              }
            } else {
              // 如果不存在，创建新关系并删除旧关系
              DepartmentUser targetUser = new DepartmentUser();
              targetUser.setDepartmentId(targetDepartmentId);
              targetUser.setUserId(userId);
              targetUser.setIsPrimary(sourceUser.getIsPrimary());
              targetUser.setIsManager(false); // 转移后不再是负责人
              insert(targetUser);
              departmentUserRepo.delete(sourceUser);
            }
            successCount++;
          }
        }

        // 更新用户主部门信息
        userCmd.updateMainDepartment(sourceDepartmentId, targetDepartmentId, userIds);
        return successCount;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void setPrimaryDepartment(Long userId, Long departmentId) {
    new BizTemplate<Void>() {
      User user;
      Department department;

      @Override
      protected void checkParams() {
        user = userQuery.findAndCheck(userId);
        department = departmentQuery.findAndCheck(departmentId);

        // 检查关系是否存在
        if (!departmentUserQuery.existsByDepartmentIdAndUserId(departmentId, userId)) {
          throw ProtocolException.of("用户「{0}」不属于该部门「{0}」",
              new Object[]{user.getName(), department.getName()});
        }
      }

      @Override
      protected Void process() {
        // 取消用户的其他主部门设置
        var currentPrimaryOpt = departmentUserQuery.findPrimaryByUserId(userId);
        if (currentPrimaryOpt.isPresent()) {
          DepartmentUser currentPrimary = currentPrimaryOpt.get();
          currentPrimary.setIsPrimary(false);
          departmentUserRepo.save(currentPrimary);
        }

        // 设置新的主部门
        var userOpt = departmentUserQuery.findByDepartmentIdAndUserId(departmentId, userId);
        if (userOpt.isPresent()) {
          DepartmentUser deptUser = userOpt.get();
          deptUser.setIsPrimary(true);
          departmentUserRepo.save(deptUser);

          // 更新用户主部门信息
          user.setDepartmentId(deptUser.getDepartmentId());
          userCmd.update0(user);

          // 记录操作日志
          String userName = user.getName() != null ? user.getName() : user.getUsername();
          String deptName = department.getName();
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.ORGANIZATION,
              departmentId,
              deptName,
              OperationMessage.DEPARTMENT_USER_SET_PRIMARY_DETAILS,
              new Object[]{userName, deptName}
          );
        }
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void removeUser(Long departmentId, Long userId) {
    new BizTemplate<Void>() {
      User user;
      Department department;

      @Override
      protected void checkParams() {
        user = userQuery.findAndCheck(userId);
        department = departmentQuery.findAndCheck(departmentId);

        // 检查关系是否存在
        if (!departmentUserQuery.existsByDepartmentIdAndUserId(departmentId, userId)) {
          throw ProtocolException.of("用户「{0}」不属于该部门「{0}」",
              new Object[]{user.getName(), department.getName()});
        }
      }

      @Override
      protected Void process() {
        departmentUserRepo.deleteByDepartmentIdAndUserId(departmentId, userId);

        // 清除用户主部门信息
        if (departmentId.equals(user.getDepartmentId())) {
          user.setDepartmentId(null);
          userCmd.update0(user);
        }

        // 发送移除部门通知
        notificationHelperCmd.createByMessageKey(
            NotificationType.WARNING,
            NotificationMessage.DEPARTMENT_MEMBER_REMOVED_TITLE,
            NotificationMessage.DEPARTMENT_MEMBER_REMOVED_DESCRIPTION,
            NotificationMessage.CATEGORY_DEPARTMENT_MANAGEMENT,
            NotificationPriority.MEDIUM,
            userId,
            new Object[]{department.getName()},
            new Object[]{department.getName()}
        );

        // 记录操作日志
        String userName = user.getName() != null ? user.getName() : user.getUsername();
        String deptName =
            department.getName() != null ? department.getName() : "部门_" + departmentId;
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.ORGANIZATION,
            departmentId,
            deptName,
            OperationMessage.DEPARTMENT_USER_REMOVE_DETAILS,
            new Object[]{deptName, userName}
        );

        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void removeUsers(Long departmentId, List<Long> userIds) {
    new BizTemplate<Void>() {
      Department department;

      @Override
      protected void checkParams() {
        department = departmentQuery.findAndCheck(departmentId);
      }

      @Override
      protected Void process() {
        departmentUserRepo.deleteByDepartmentIdAndUserIdIn(departmentId, userIds);
        userCmd.clearMainDepartment(userIds, departmentId);

        // 发送移除部门通知给被移除的用户
        if (!userIds.isEmpty()) {
          notificationHelperCmd.createBatchByMessageKey(
              NotificationType.WARNING,
              NotificationMessage.DEPARTMENT_MEMBER_REMOVED_TITLE,
              NotificationMessage.DEPARTMENT_MEMBER_REMOVED_DESCRIPTION,
              NotificationMessage.CATEGORY_DEPARTMENT_MANAGEMENT,
              NotificationPriority.MEDIUM,
              userIds,
              new Object[]{department.getName()},
              new Object[]{department.getName()}
          );

          // 记录操作日志
          String deptName = department.getName();
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.DELETE,
              ResourceType.ORGANIZATION,
              departmentId,
              deptName,
              OperationMessage.DEPARTMENT_USER_REMOVE_BATCH_DETAILS,
              new Object[]{deptName, userIds.size()}
          );
        }
        return null;
      }
    }.execute();
  }

  @Override
  public void deleteByDepartmentId(Long departmentId) {
    departmentUserRepo.deleteByDepartmentId(departmentId);
    userCmd.clearMainDepartment(departmentId);
  }

  @Override
  public void deleteByUserIds(Collection<Long> ids) {
    departmentUserRepo.deleteAllByUserIdIn(ids);
  }

  @Override
  protected BaseRepository<DepartmentUser, Long> getRepository() {
    return departmentUserRepo;
  }
}
