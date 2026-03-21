package cloud.xcan.angus.core.gm.application.cmd.role.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.commonlink.application.ApplicationMenu;
import cloud.xcan.angus.api.commonlink.role.PermissionInfo;
import cloud.xcan.angus.api.commonlink.role.Role;
import cloud.xcan.angus.api.commonlink.role.RoleRepo;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.authorization.AuthorizationCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.role.RoleCmd;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationMenuQuery;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.application.query.authorization.AuthorizationQuery;
import cloud.xcan.angus.core.gm.application.query.role.RoleQuery;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.core.utils.PrincipalContextUtils;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleCmdImpl extends CommCmd<Role, Long> implements RoleCmd {

  @Resource
  private RoleRepo roleRepo;

  @Resource
  private RoleQuery roleQuery;

  @Resource
  private ApplicationQuery applicationQuery;

  @Resource
  private ApplicationMenuQuery applicationMenuQuery;

  @Resource
  private AuthorizationQuery authorizationQuery;

  @Resource
  private AuthorizationCmd authorizationCmd;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Role create(Role role) {
    return new BizTemplate<Role>() {
      List<ApplicationMenu> menus;

      @Override
      protected void checkParams() {
        // 检查应用是否存在
        Application application = applicationQuery.findAndCheck(role.getAppId());
        PrincipalContextUtils.setMultiTenantCtrl(true);

        if (roleRepo.existsByName(role.getName())) {
          throw ResourceExisted.of("角色名称「{0}」已存在", new Object[]{role.getName()});
        }

        if (role.getCode() != null && roleRepo.existsByCode(role.getCode())) {
          throw ResourceExisted.of("角色编码「{0}」已存在", new Object[]{role.getCode()});
        }

        if (Boolean.TRUE.equals(role.getIsDefault()) && role.getAppId() != null) {
          Role existingDefault = roleQuery.findByAppIdAndIsDefaultTrue(role.getAppId());
          if (existingDefault != null) {
            throw ResourceExisted.of("应用「{0}」已存在默认角色",
                new Object[]{application.getCode()});
          }
        }

        // 检查授权应用菜单是否存在
        if (isNotEmpty(role.getPermissions())) {
          menus = applicationMenuQuery.findAndCheck(role.getAppId(),
              role.getPermissions().stream().map(PermissionInfo::getMenuId)
                  .filter(Objects::nonNull).collect(Collectors.toSet()));
        }
      }

      @Override
      protected Role process() {
        assemblePermissionMenu(menus, role.getPermissions());

        insert(role);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.PERMISSION,
            role.getId(),
            role.getName(),
            OperationMessage.ROLE_CREATE_DETAILS,
            new Object[]{role.getName()}
        );
        return role;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Role update(Role role) {
    return new BizTemplate<Role>() {
      Role existing;
      List<ApplicationMenu> menus;

      @Override
      protected void checkParams() {
        // 检查角色是否存在
        existing = roleQuery.findAndCheck(role.getId());

        // 校验name唯一性（如果name有变化）
        if (role.getName() != null && !role.getName().equals(existing.getName())) {
          if (roleRepo.existsByNameAndIdNot(role.getName(), role.getId())) {
            throw ResourceExisted.of("角色名称「{0}」已存在", new Object[]{role.getName()});
          }
        }

        // 检查应用是否存在
        Application application = applicationQuery.findAndCheck(existing.getAppId());
        PrincipalContextUtils.setMultiTenantCtrl(true);

        // 校验默认角色唯一性
        if (Boolean.TRUE.equals(role.getIsDefault()) && existing.getAppId() != null) {
          Role existingDefault = roleQuery.findByAppIdAndIsDefaultTrue(existing.getAppId());
          if (existingDefault != null && !existingDefault.getId().equals(role.getId())) {
            throw ResourceExisted.of("应用「{0}」已存在默认角色",
                new Object[]{application.getCode()});
          }
        }

        // 检查授权应用菜单是否存在
        if (isNotEmpty(role.getPermissions())) {
          menus = applicationMenuQuery.findAndCheck(existing.getAppId(),
              role.getPermissions().stream().map(PermissionInfo::getMenuId)
                  .filter(Objects::nonNull).collect(Collectors.toSet()));
        }
      }

      @Override
      protected Role process() {
        assemblePermissionMenu(menus, role.getPermissions());

        CoreUtils.copyPropertiesIgnoreNull(role, existing);
        Role saved = roleRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.PERMISSION,
            saved.getId(),
            saved.getName(),
            OperationMessage.ROLE_UPDATE_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  public Role updateStatus(Long id, EnabledStatus status) {
    return new BizTemplate<Role>() {
      Role existing;

      @Override
      protected void checkParams() {
        // 检查角色是否存在
        existing = roleQuery.findAndCheck(id);
      }

      @Override
      protected Role process() {
        existing.setStatus(status);
        Role saved = roleRepo.save(existing);

        // 记录操作日志
        String statusText = status.isEnabled() ? "启用" : "禁用";
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.PERMISSION,
            saved.getId(),
            saved.getName(),
            OperationMessage.ROLE_UPDATE_STATUS_DETAILS,
            new Object[]{saved.getName(), statusText}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      Role existing;

      @Override
      protected void checkParams() {
        existing = roleQuery.findAndCheck(id);
        if (Boolean.TRUE.equals(existing.getIsSystem())) {
          throw ResourceNotFound.of("系统角色不能删除", new Object[]{});
        }
        long subjectCount = authorizationQuery.countByRoleId(id);
        if (subjectCount > 0) {
          throw ResourceExisted.of("角色「{0}」下存在授权，无法删除", new Object[]{id});
        }
      }

      @Override
      protected Void process() {
        String roleName = existing.getName();
        roleRepo.deleteById(id);
        authorizationCmd.deleteByRoleId(id);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.PERMISSION,
            id,
            roleName,
            OperationMessage.ROLE_DELETE_DETAILS,
            new Object[]{roleName}
        );

        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Role updatePermissions(Long id, List<PermissionInfo> permissions) {
    return new BizTemplate<Role>() {
      Role existing;
      List<ApplicationMenu> menus;

      @Override
      protected void checkParams() {
        existing = roleQuery.findAndCheck(id);

        // 检查授权应用菜单是否存在
        if (isNotEmpty(permissions)) {
          menus = applicationMenuQuery.findAndCheck(existing.getAppId(),
              permissions.stream().map(PermissionInfo::getMenuId)
                  .filter(Objects::nonNull).collect(Collectors.toSet()));
        }
      }

      @Override
      protected Role process() {
        assemblePermissionMenu(menus, permissions);

        existing.setPermissions(permissions);
        Role saved = roleRepo.save(existing);

        // 记录操作日志
        int permissionCount = permissions != null ? permissions.size() : 0;
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.PERMISSION,
            saved.getId(),
            saved.getName(),
            OperationMessage.ROLE_UPDATE_PERMISSIONS_DETAILS,
            new Object[]{saved.getName(), permissionCount}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Role setDefault(Long id, Boolean isDefault) {
    return new BizTemplate<Role>() {
      Role roleDb;

      @Override
      protected void checkParams() {
        roleDb = roleQuery.findAndCheck(id);
        if (Boolean.TRUE.equals(isDefault) && roleDb.getAppId() != null) {
          Role existingDefault = roleQuery.findByAppIdAndIsDefaultTrue(roleDb.getAppId());
          if (existingDefault != null && !existingDefault.getId().equals(id)) {
            throw ResourceExisted.of("应用「{0}」已存在默认角色", new Object[]{roleDb.getAppId()});
          }
        }
      }

      @Override
      protected Role process() {
        roleDb.setIsDefault(isDefault);
        Role saved = roleRepo.save(roleDb);

        // 记录操作日志
        String isDefaultText = Boolean.TRUE.equals(isDefault) ? "YES" : "NO";
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.PERMISSION,
            saved.getId(),
            saved.getName(),
            OperationMessage.ROLE_SET_DEFAULT_DETAILS,
            new Object[]{saved.getName(), isDefaultText}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  public void update0(Role role) {
    roleRepo.save(role);
  }

  @Override
  public void deleteByApplicationId(Long appId) {
    boolean multiTenantCtrl = PrincipalContextUtils.isMultiTenantCtrl();
    try {
      // 删除所有应用租户自定义角色
      PrincipalContextUtils.setMultiTenantCtrl(false);
      roleRepo.deleteByAppId(appId);

      // 删除所有应用租户授权
      List<Long> roleIds = roleRepo.findWideRolesByAppIdAndTenantId(appId, getOptTenantId())
          .stream().map(Role::getId).collect(Collectors.toList());
      if (!roleIds.isEmpty()) {
        authorizationCmd.deleteByRoleIdIn(roleIds);
      }
    } finally {
      if (multiTenantCtrl) {
        PrincipalContextUtils.setMultiTenantCtrl(true);
      }
    }
  }

  private static void assemblePermissionMenu(List<ApplicationMenu> menus,
      List<PermissionInfo> permissions) {
    if (nonNull(menus)) {
      Map<Long, ApplicationMenu> menuMap = menus.stream()
          .collect(Collectors.toMap(ApplicationMenu::getId, x -> x));
      for (PermissionInfo permission : permissions) {
        if (permission.getMenuId() != null) {
          ApplicationMenu menu = menuMap.get(permission.getMenuId());
          if (menu != null) {
            permission.setParentMenuId(menu.getParentId());
            permission.setMenuName(menu.getName());
          }
        }
      }
    }
  }

  @Override
  protected BaseRepository<Role, Long> getRepository() {
    return roleRepo;
  }
}
