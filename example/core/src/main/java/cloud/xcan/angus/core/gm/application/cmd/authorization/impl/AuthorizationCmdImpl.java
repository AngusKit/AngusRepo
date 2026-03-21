package cloud.xcan.angus.core.gm.application.cmd.authorization.impl;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.commonlink.department.DepartmentUserRepo;
import cloud.xcan.angus.api.commonlink.group.GroupUserRepo;
import cloud.xcan.angus.api.commonlink.role.Role;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.authorization.AuthorizationCmd;
import cloud.xcan.angus.core.gm.application.cmd.notification.NotificationHelperCmd;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.application.query.authorization.AuthorizationQuery;
import cloud.xcan.angus.core.gm.application.query.role.RoleQuery;
import cloud.xcan.angus.core.gm.domain.authorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRepo;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRole;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRoleRepo;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.core.gm.domain.notification.NotificationMessage;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationPriority;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorizationCmdImpl extends CommCmd<Authorization, Long> implements AuthorizationCmd {

  @Resource
  private AuthorizationRepo authorizationRepo;

  @Resource
  private AuthorizationQuery authorizationQuery;

  @Resource
  private AuthorizationRoleRepo authorizationRoleRepo;

  @Resource
  private RoleQuery roleQuery;

  @Resource
  private NotificationHelperCmd notificationHelperCmd;

  @Resource
  private DepartmentUserRepo departmentUserRepo;

  @Resource
  private GroupUserRepo groupUserRepo;

  @Resource
  private ApplicationQuery applicationQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Authorization create(Authorization authorization) {
    return new BizTemplate<Authorization>() {
      List<Long> roleIds;
      List<Role> roles;
      String subjectName;

      @Override
      protected void checkParams() {
        // 从 transient 字段获取角色ID列表（由 Assembler 设置）
        roleIds = getRoleIdsFromAuthorization(authorization);
        // 检查角色是否存在
        roles = roleQuery.findAndCheck(roleIds);
        // TODO 检查用户是否有指定角色授权权限
        // 检查授权主体是否存在
        subjectName = authorizationQuery.checkSubjectExists(
            authorization.getSubjectType(), authorization.getSubjectId());
        // 检查授权主体是否存在
        if (authorizationRepo.existsBySubjectTypeAndSubjectId(
            authorization.getSubjectType(), authorization.getSubjectId())) {
          throw ResourceExisted.of("授权主体「{0}」授权已存在", new Object[]{subjectName});
        }
      }

      @Override
      protected Authorization process() {
        // 云服务版开通时，由运营端应用调用开通业务授权
        // 私有化安装时，由安装业务初始化应用授权
        authorization.setSubjectName(subjectName);
        authorization.setOpened(false);
        insert(authorization);

        // 创建授权与角色关系记录
        if (roleIds != null && !roleIds.isEmpty()) {
          createAuthorizationRoles(authorization.getId(), authorization.getTenantId(), roleIds);
        }

        // 发送授权创建通知
        sendAuthorizationNotification(authorization, roles,
            NotificationMessage.AUTHORIZATION_CREATED_TITLE,
            NotificationMessage.AUTHORIZATION_CREATED_DESCRIPTION);

        return authorization;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Authorization update(Authorization authorization) {
    return new BizTemplate<Authorization>() {
      Authorization existing;
      List<Long> roleIds;
      List<Role> roles;

      @Override
      protected void checkParams() {
        // 检查授权是否存在
        existing = authorizationQuery.findAndCheck(authorization.getId());
        // 从 transient 字段获取角色ID列表
        roleIds = getRoleIdsFromAuthorization(authorization);
        // 检查角色是否存在
        roles = roleQuery.findAndCheck(roleIds);
        // 检查授权主体是否存在（如果主体类型或ID发生变化）
        if (authorization.getSubjectType() != null && authorization.getSubjectId() != null) {
          if (!existing.getSubjectType().equals(authorization.getSubjectType())
              || !existing.getSubjectId().equals(authorization.getSubjectId())) {
            authorizationQuery.checkSubjectExists(authorization.getSubjectType(),
                authorization.getSubjectId());
          }
        }
      }

      @Override
      protected Authorization process() {
        // 更新授权基本信息
        if (authorization.getOpened() != null) {
          existing.setOpened(authorization.getOpened());
        }
        if (authorization.getValidFrom() != null) {
          existing.setValidFrom(authorization.getValidFrom());
        }
        if (authorization.getValidTo() != null) {
          existing.setValidTo(authorization.getValidTo());
        }
        if (authorization.getDescription() != null) {
          existing.setDescription(authorization.getDescription());
        }
        authorization.setOpened(false);
        authorizationRepo.save(existing);

        // 重置授权与角色关系记录
        authorizationRoleRepo.deleteByAuthorizationId(existing.getId());
        if (roleIds != null && !roleIds.isEmpty()) {
          List<Long> distinctRoleIds = roleIds.stream().distinct().collect(Collectors.toList());
          createAuthorizationRoles(existing.getId(), existing.getTenantId(), distinctRoleIds);
        }

        // 发送授权更新通知
        sendAuthorizationNotification(existing, roles,
            NotificationMessage.AUTHORIZATION_UPDATED_TITLE,
            NotificationMessage.AUTHORIZATION_UPDATED_DESCRIPTION);

        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Authorization addRoles(Long id, List<Long> roleIds) {
    return new BizTemplate<Authorization>() {
      Authorization existing;
      List<Role> roles;

      @Override
      protected void checkParams() {
        // 检查授权是否存在
        existing = authorizationQuery.findAndCheck(id);
        // 检查角色是否存在
        roles = roleQuery.findAndCheck(roleIds);
      }

      @Override
      protected Authorization process() {
        // 获取已存在的角色ID列表
        List<AuthorizationRole> existingRoles = authorizationRoleRepo.findByAuthorizationId(id);
        List<Long> existedRoleIds = existingRoles.stream()
            .map(AuthorizationRole::getRoleId)
            .collect(Collectors.toList());

        // 添加新角色（去重）
        List<Long> roleIdsList = nullSafe(roleIds, new ArrayList<>());
        List<Long> newRoleIds = roleIdsList.stream()
            .filter(roleId -> !existedRoleIds.contains(roleId))
            .distinct()
            .collect(Collectors.toList());

        // 创建新的授权角色关系
        if (!newRoleIds.isEmpty()) {
          createAuthorizationRoles(id, existing.getTenantId(), newRoleIds);
          // 查询新添加的角色信息
          List<Role> newRoles = roleQuery.findAndCheck(newRoleIds);
          // 发送角色添加通知
          sendAuthorizationNotification(existing, newRoles,
              NotificationMessage.AUTHORIZATION_UPDATED_TITLE,
              NotificationMessage.AUTHORIZATION_UPDATED_DESCRIPTION);
        }
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Authorization removeRole(Long id, Long roleId) {
    return new BizTemplate<Authorization>() {
      Authorization existing;

      @Override
      protected void checkParams() {
        // 检查授权是否存在
        existing = authorizationQuery.findAndCheck(id);
      }

      @Override
      protected Authorization process() {
        // 删除授权角色关系
        authorizationRoleRepo.deleteByAuthorizationIdAndRoleId(id, roleId);
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void batchDelete(List<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // 检查所有授权是否存在，并验证是否允许删除
        for (Long id : ids) {
          Authorization authorization = authorizationQuery.findAndCheck(id);
          // 检查如果是自动授权（opened=true），不允许删除
          if (Boolean.TRUE.equals(authorization.getOpened())) {
            throw ProtocolException.of("自动授权「{0}」不允许删除",
                new Object[]{authorization.getSubjectName()});
          }
        }
      }

      @Override
      protected Void process() {
        // 删除授权与角色关系记录
        authorizationRoleRepo.deleteAllByAuthorizationIdIn(ids);
        // 使用 bulk delete 替代 deleteAllById，避免记录已被级联删除时触发 StaleStateException
        if (!ids.isEmpty()) {
          authorizationRepo.deleteByIdIn(ids);
        }
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      Authorization existing;

      @Override
      protected void checkParams() {
        // 检查授权是否存在
        existing = authorizationQuery.findAndCheck(id);
        // 检查如果是自动授权（opened=true），不允许删除
        if (Boolean.TRUE.equals(existing.getOpened())) {
          throw ProtocolException.of("自动授权「{0}」不允许删除",
              new Object[]{existing.getSubjectName()});
        }
      }

      @Override
      protected Void process() {
        // 删除授权与角色关系记录
        authorizationRoleRepo.deleteByAuthorizationId(id);
        // 使用 bulk delete 替代 deleteById，避免记录已被级联删除时触发 StaleStateException
        authorizationRepo.deleteByIdIfExists(id);
        return null;
      }
    }.execute();
  }

  @Override
  public void deleteByRoleId(Long roleId) {
    authorizationRoleRepo.deleteByRoleId(roleId);
  }

  @Override
  public void deleteByRoleIdIn(List<Long> roleIds) {
    authorizationRoleRepo.deleteByRoleIdIn(roleIds);
  }

  @Override
  public void deleteBySubjectTypeAndId(AuthorizationSubjectType subjectType, Long subjectId) {
    // 先删除授权角色关系，再批量删除授权（使用 bulk delete 替代 deleteById，避免记录已被级联删除时触发 StaleStateException）
    List<Long> authorizationIds = authorizationRepo.findIdsBySubjectTypeAndSubjectId(subjectType,
        subjectId);
    if (!authorizationIds.isEmpty()) {
      authorizationRoleRepo.deleteAllByAuthorizationIdIn(authorizationIds);
    }
    authorizationRepo.deleteBySubjectTypeAndSubjectId(subjectType, subjectId);
  }

  /**
   * 创建授权与角色关系
   */
  private void createAuthorizationRoles(Long authorizationId, Long tenantId, List<Long> roleIds) {
    if (roleIds == null || roleIds.isEmpty()) {
      return;
    }
    for (Long roleId : roleIds) {
      if (!authorizationRoleRepo.existsByAuthorizationIdAndRoleId(authorizationId, roleId)) {
        AuthorizationRole authorizationRole = new AuthorizationRole();
        authorizationRole.setId(uidGenerator.getUID());
        authorizationRole.setAuthorizationId(authorizationId);
        authorizationRole.setRoleId(roleId);
        authorizationRole.setTenantId(tenantId);
        authorizationRoleRepo.save(authorizationRole);
      }
    }
  }

  /**
   * 从授权实体的临时字段获取角色ID列表，该字段由 Assembler 设置
   */
  private List<Long> getRoleIdsFromAuthorization(Authorization authorization) {
    return nullSafe(authorization.getRoleIds(), new ArrayList<>());
  }

  /**
   * 根据授权主体类型获取需要通知的用户ID列表
   */
  private List<Long> getNotifyUserIds(Authorization authorization) {
    AuthorizationSubjectType subjectType = authorization.getSubjectType();
    Long subjectId = authorization.getSubjectId();

    if (subjectType == null || subjectId == null) {
      return new ArrayList<>();
    }

    switch (subjectType) {
      case USER:
        // 用户类型：直接返回用户ID
        return List.of(subjectId);
      case DEPARTMENT:
        // 部门类型：获取部门下所有用户ID
        Set<Long> deptUserIds = departmentUserRepo.findUserIdsByDeptIds(List.of(subjectId));
        return new ArrayList<>(deptUserIds);
      case GROUP:
        // 组类型：获取组下所有用户ID
        Set<Long> groupUserIds = groupUserRepo.findUserIdsByGroupIds(List.of(subjectId));
        return new ArrayList<>(groupUserIds);
      default:
        return new ArrayList<>();
    }
  }

  /**
   * 获取授权相关的应用和角色信息
   */
  private String getAppAndRoleInfo(List<Role> roles) {
    if (roles == null || roles.isEmpty()) {
      return "";
    }

    // 获取应用ID集合
    Set<Long> appIds = roles.stream()
        .map(Role::getAppId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    // 查询应用信息
    List<String> appNames = new ArrayList<>();
    if (!appIds.isEmpty()) {
      List<Application> applications = applicationQuery.findAllById(new ArrayList<>(appIds));
      appNames = applications.stream()
          .map(app -> app.getName() != null ? app.getName() : app.getCode())
          .filter(Objects::nonNull)
          .distinct()
          .collect(Collectors.toList());
    }

    // 获取角色名称
    List<String> roleNames = roles.stream()
        .map(role -> role.getName() != null ? role.getName() : role.getCode())
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());

    // 组装信息
    StringBuilder info = new StringBuilder();
    if (!appNames.isEmpty()) {
      info.append("应用：").append(String.join("、", appNames));
    }
    if (!roleNames.isEmpty()) {
      if (!info.isEmpty()) {
        info.append("，");
      }
      info.append("角色：").append(String.join("、", roleNames));
    }
    return info.toString();
  }

  /**
   * 发送授权通知
   */
  private void sendAuthorizationNotification(Authorization authorization, List<Role> roles,
      String titleKey, String descriptionKey) {
    try {
      // 获取需要通知的用户ID列表
      List<Long> notifyUserIds = getNotifyUserIds(authorization);
      if (notifyUserIds.isEmpty()) {
        return;
      }

      // 获取应用和角色信息
      String appAndRoleInfo = getAppAndRoleInfo(roles);
      String subjectName = authorization.getSubjectName() != null
          ? authorization.getSubjectName()
          : String.valueOf(authorization.getSubjectId());

      // 批量发送通知
      // 根据消息键决定参数：创建通知需要应用和角色信息，更新通知也需要应用和角色信息
      Object[] descriptionArgs = new Object[]{subjectName,
          appAndRoleInfo.isEmpty() ? "" : appAndRoleInfo};

      notificationHelperCmd.createBatchByMessageKey(
          NotificationType.SUCCESS,
          titleKey,
          descriptionKey,
          NotificationMessage.CATEGORY_AUTHORIZATION_MANAGEMENT,
          NotificationPriority.MEDIUM,
          notifyUserIds,
          new Object[]{subjectName},
          descriptionArgs
      );
    } catch (Exception e) {
      // 通知发送失败不影响主流程
    }
  }

  @Override
  protected BaseRepository<Authorization, Long> getRepository() {
    return authorizationRepo;
  }

}
