package cloud.xcan.angus.core.gm.application.cmd.department.impl;

import static cloud.xcan.angus.spec.principal.PrincipalContext.get;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.QuotaConstant;
import cloud.xcan.angus.api.commonlink.department.Department;
import cloud.xcan.angus.api.commonlink.department.DepartmentRepo;
import cloud.xcan.angus.api.commonlink.department.DepartmentUser;
import cloud.xcan.angus.api.manager.QuotaManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.authorization.AuthorizationCmd;
import cloud.xcan.angus.core.gm.application.cmd.department.DepartmentCmd;
import cloud.xcan.angus.core.gm.application.cmd.department.DepartmentUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.notification.NotificationHelperCmd;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentQuery;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentUserQuery;
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
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentCmdImpl extends CommCmd<Department, Long> implements DepartmentCmd {

  @Resource
  private DepartmentRepo departmentRepo;

  @Resource
  private DepartmentQuery departmentQuery;

  @Resource
  private UserQuery userQuery;

  @Resource
  private DepartmentUserCmd departmentUserCmd;

  @Resource
  private DepartmentUserQuery departmentUserQuery;

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
  public Department create(Department department) {
    return new BizTemplate<Department>() {
      @Override
      protected void checkParams() {
        // 检查部门名称是否存在
        if (departmentRepo.existsByName(department.getName())) {
          throw ResourceExisted.of("部门名称「{0}」已存在", new Object[]{department.getName()});
        }

        // 检查部门编码是否存在
        if (departmentRepo.existsByCode(department.getCode())) {
          throw ResourceExisted.of("部门编码「{0}」已存在", new Object[]{department.getCode()});
        }

        // 校验部门管理者是否存在
        if (department.getLeaderId() != null) {
          userQuery.findAndCheck(department.getLeaderId());
        }

        // 校验并计算部门层级
        if (department.getParentId() != null) {
          Department parent = departmentQuery.findAndCheck(department.getParentId());
          // 检查是否存在循环依赖
          checkCircularDependency(department.getParentId(), department.getId());
          department.setLevel(parent.getLevel() + 1);
        } else {
          department.setLevel(1);
        }
      }

      @Override
      protected Department process() {
        insert(department);

        // 如果有父部门，递归更新所有子部门的层级
        if (department.getParentId() != null) {
          updateChildrenLevels(department.getId(), department.getLevel());
        }

        // 增加部门配额使用量
        quotaManager.increaseTenantQuota(
            tenantQuery.getMainTenantOfSameAccount(department.getTenantId()).getId(),
            QuotaConstant.QuotaDepartmentCount, 1L);

        // 记录操作日志
        String departmentName = department.getName();
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.ORGANIZATION,
            department.getId(),
            departmentName,
            OperationMessage.DEPARTMENT_CREATE_DETAILS,
            new Object[]{departmentName}
        );
        return department;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Department update(Department department) {
    return new BizTemplate<Department>() {
      Department existed;

      @Override
      protected void checkParams() {
        // 检查修改部门是否存在
        existed = departmentQuery.findAndCheck(department.getId());

        // 检查部门名称是否存在
        if (departmentRepo.existsByNameAndIdNot(department.getName(), department.getId())) {
          throw ResourceExisted.of("部门名称「{0}」已存在", new Object[]{department.getName()});
        }

        // 检查部门编码是否存在
        if (departmentRepo.existsByCodeAndIdNot(department.getCode(), department.getId())) {
          throw ResourceExisted.of("部门编码「{0}」已存在", new Object[]{department.getCode()});
        }

        // 校验部门管理者是否存在
        if (department.getLeaderId() != null) {
          userQuery.findAndCheck(department.getLeaderId());
        }

        // 校验并计算部门层级
        if (department.getParentId() != null) {
          Department parent = departmentQuery.findAndCheck(department.getParentId());
          // 检查是否存在循环依赖
          checkCircularDependency(department.getParentId(), department.getId());
          // 如果父部门发生变化，需要检查是否形成循环依赖
          if (!Objects.equals(existed.getParentId(), department.getParentId())) {
            checkCircularDependency(department.getParentId(), department.getId());
          }
          department.setLevel(parent.getLevel() + 1);
        } else {
          department.setLevel(1);
        }
      }

      @Override
      protected Department process() {
        // 保存原始父部门ID用于后续层级更新
        Long originalParentId = existed.getParentId();
        Integer originalLevel = existed.getLevel();

        if (nonNull(department.getName())) {
          existed.setName(department.getName());
        }
        if (nonNull(department.getCode())) {
          existed.setCode(department.getCode());
        }
        existed.setParentId(department.getParentId());
        // 更新层级：在checkParams()中已根据parentId计算好level
        if (nonNull(department.getLevel())) {
          existed.setLevel(department.getLevel());
        }
        existed.setLeaderId(department.getLeaderId());
        existed.setDescription(department.getDescription());
        if (nonNull(department.getSortOrder())) {
          existed.setSortOrder(department.getSortOrder());
        }
        if (nonNull(department.getStatus())) {
          existed.setStatus(department.getStatus());
        }
        departmentRepo.save(existed);

        // 如果父部门发生变化，需要更新相关层级
        if (originalParentId == null && existed.getParentId() != null) {
          // 从根部门变为子部门
          updateChildrenLevels(existed.getId(), existed.getLevel());
        } else if (originalParentId != null && existed.getParentId() == null) {
          // 从子部门变为根部门
          updateChildrenLevels(existed.getId(), 1);
        } else if (originalParentId != null && !originalParentId.equals(existed.getParentId())) {
          // 父部门发生变化
          updateChildrenLevels(existed.getId(), existed.getLevel());
        } else if (!originalLevel.equals(existed.getLevel())) {
          // 层级发生变化时更新子部门
          updateChildrenLevels(existed.getId(), existed.getLevel());
        }

        // 记录操作日志
        String departmentName = existed.getName();
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.ORGANIZATION,
            existed.getId(),
            departmentName,
            OperationMessage.DEPARTMENT_UPDATE_DETAILS,
            new Object[]{departmentName}
        );
        return existed;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Department updateStatus(Long id, EnabledStatus status) {
    return new BizTemplate<Department>() {
      Department existed;

      @Override
      protected void checkParams() {
        existed = departmentQuery.findAndCheck(id);
      }

      @Override
      protected Department process() {
        existed.setStatus(status);
        departmentRepo.save(existed);

        // 记录操作日志
        String departmentName = existed.getName();
        if (EnabledStatus.ENABLED.equals(status)) {
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.ORGANIZATION,
              id,
              departmentName,
              OperationMessage.DEPARTMENT_ENABLE_DETAILS,
              new Object[]{departmentName}
          );
        } else if (EnabledStatus.DISABLED.equals(status)) {
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.ORGANIZATION,
              id,
              departmentName,
              OperationMessage.DEPARTMENT_DISABLE_DETAILS,
              new Object[]{departmentName}
          );
        }
        return existed;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Department updateLeader(Long id, Long leaderId) {
    return new BizTemplate<Department>() {
      Department existed;

      @Override
      protected void checkParams() {
        existed = departmentQuery.findAndCheck(id);
        if (leaderId != null) {
          userQuery.findAndCheck(leaderId);
        }
      }

      @Override
      protected Department process() {
        existed.setLeaderId(leaderId);
        departmentRepo.save(existed);

        // 记录操作日志
        String departmentName = existed.getName();
        String managerName = leaderId != null ? userQuery.findAndCheck(leaderId).getName() : "无";
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.ORGANIZATION,
            id,
            departmentName,
            OperationMessage.DEPARTMENT_UPDATE_MANAGER_DETAILS,
            new Object[]{departmentName, managerName}
        );
        return existed;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      Department existed;
      List<Long> userIds;

      @Override
      protected void checkParams() {
        // 检查修改部门是否存在
        existed = departmentQuery.findAndCheck(id);

        // 检查是否有子级
        long childCount = departmentRepo.countByParentId(id);
        if (childCount > 0) {
          throw ResourceExisted.of("部门下存在子部门，无法删除", new Object[]{});
        }

        // 获取部门下的所有用户ID
        userIds = departmentUserQuery.findByDepartmentId(id).stream()
            .map(DepartmentUser::getUserId)
            .collect(Collectors.toList());
      }

      @Override
      protected Void process() {
        // 减少部门配额使用量
        quotaManager.decreaseTenantQuota(
            tenantQuery.getMainTenantOfSameAccount(get().getOptTenantId()).getId(),
            QuotaConstant.QuotaDepartmentCount, 1L);

        // 删除部门用户关系
        departmentUserCmd.deleteByDepartmentId(id);
        // 删除部门
        departmentRepo.deleteById(id);
        // 删除部门授权
        authorizationCmd.deleteBySubjectTypeAndId(AuthorizationSubjectType.DEPARTMENT, id);

        // 发送部门解散通知给部门下所有用户
        if (!userIds.isEmpty()) {
          notificationHelperCmd.createBatchByMessageKey(
              NotificationType.WARNING,
              NotificationMessage.DEPARTMENT_DELETED_TITLE,
              NotificationMessage.DEPARTMENT_DELETED_DESCRIPTION,
              NotificationMessage.CATEGORY_DEPARTMENT_MANAGEMENT,
              NotificationPriority.HIGH,
              userIds,
              new Object[]{existed.getName()},
              new Object[]{existed.getName()}
          );
        }

        // 记录操作日志
        String departmentName = existed.getName();
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.ORGANIZATION,
            id,
            departmentName,
            OperationMessage.DEPARTMENT_DELETE_DETAILS,
            new Object[]{departmentName}
        );
        return null;
      }
    }.execute();
  }

  @Override
  public void deleteByTenantId(Long tenantId) {
    departmentRepo.deleteByTenantId(tenantId);
    quotaManager.resetTenantQuota(tenantQuery.getMainTenantOfSameAccount(tenantId).getId(),
        QuotaConstant.QuotaDepartmentCount);
  }

  /**
   * 检查是否存在循环依赖
   *
   * @param parentId 父部门ID
   * @param childId  子部门ID
   */
  private void checkCircularDependency(Long parentId, Long childId) {
    if (parentId == null || parentId.equals(childId)) {
      throw ResourceExisted.of("不能将部门设置为自己的父部门", new Object[]{});
    }

    // 检查父部门的父部门链中是否包含当前部门ID，防止循环依赖
    Long currentParentId = parentId;
    int maxDepth = 100; // 防止无限循环，设置最大深度
    int depth = 0;

    while (currentParentId != null && depth < maxDepth) {
      if (currentParentId.equals(childId)) {
        throw ResourceExisted.of("检测到部门层级循环依赖，请检查部门结构", new Object[]{});
      }

      try {
        Department parentDept = departmentQuery.findAndCheck(currentParentId);
        currentParentId = parentDept.getParentId();
        depth++;
      } catch (Exception e) {
        // 如果父部门不存在，跳出循环
        break;
      }
    }

    if (depth >= maxDepth) {
      throw ResourceExisted.of("部门层级过深，请检查部门结构", new Object[]{});
    }
  }

  /**
   * 递归更新子部门层级
   *
   * @param parentId 父部门ID
   * @param newLevel 新的层级
   */
  private void updateChildrenLevels(Long parentId, Integer newLevel) {
    List<Department> children = departmentRepo.findByParentId(parentId);
    for (Department child : children) {
      child.setLevel(newLevel + 1);
      departmentRepo.save(child);
      // 递归更新孙子部门
      updateChildrenLevels(child.getId(), child.getLevel());
    }
  }

  @Override
  protected BaseRepository<Department, Long> getRepository() {
    return departmentRepo;
  }
}
