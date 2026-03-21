package cloud.xcan.angus.core.gm.application.cmd.application.impl;

import static cloud.xcan.angus.core.gm.application.converter.AuthorizationConverter.toPermissionInfo;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.application.ApplicationMenu;
import cloud.xcan.angus.api.commonlink.application.ApplicationMenuRepo;
import cloud.xcan.angus.api.commonlink.role.PermissionInfo;
import cloud.xcan.angus.api.commonlink.role.Role;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.PermissionCheck;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.application.ApplicationMenuCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.role.RoleCmd;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationMenuQuery;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.application.query.role.RoleQuery;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationMenuCmdImpl extends CommCmd<ApplicationMenu, Long>
    implements ApplicationMenuCmd {

  @Resource
  private ApplicationMenuRepo applicationMenuRepo;

  @Resource
  private ApplicationMenuQuery applicationMenuQuery;

  @Resource
  private ApplicationQuery applicationQuery;

  @Resource
  private RoleQuery roleQuery;

  @Resource
  private RoleCmd roleCmd;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ApplicationMenu create(ApplicationMenu menu) {
    return new BizTemplate<ApplicationMenu>(false) {
      @Override
      protected void checkParams() {
        // 如果非运营租户禁止创建云应用菜单
        PermissionCheck.checkCloudTenantSecurity();
        // 验证应用是否存在
        applicationMenuQuery.findAndCheck(menu.getApplicationId());
        // 验证菜单编码唯一性
        if (applicationMenuQuery.existsByAppIdAndCode(menu.getApplicationId(), menu.getCode())) {
          throw ResourceExisted.of("菜单编码「{0}」已存在", new Object[]{menu.getCode()});
        }
        // 验证父菜单是否存在
        if (menu.getParentId() != null && menu.getParentId() > 0) {
          applicationMenuQuery.findAndCheck(menu.getParentId());
        }
      }

      @Override
      protected ApplicationMenu process() {
        menu.setId(uidGenerator.getUID());

        PermissionInfo permissionInfo = menu.getPermission() == null
            ? new PermissionInfo() : menu.getPermission();
        permissionInfo.setParentMenuId(menu.getParentId());
        permissionInfo.setMenuId(menu.getId());
        permissionInfo.setMenuName(menu.getName());
        menu.setPermission(permissionInfo);

        insert(menu);

        // 记录操作日志
        String menuName = menu.getName() != null ? menu.getName() : menu.getCode();
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.APPLICATION,
            menu.getId(),
            menuName,
            OperationMessage.APPLICATION_MENU_CREATE_DETAILS,
            new Object[]{menuName, menu.getApplicationId()}
        );

        return menu;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ApplicationMenu update(ApplicationMenu menu) {
    return new BizTemplate<ApplicationMenu>(false) {
      ApplicationMenu existing;

      @Override
      protected void checkParams() {
        // 验证菜单编码唯一性
        existing = applicationMenuQuery.findAndCheck(menu.getId());
        // 如果非运营租户，禁止修改来源是安装类型的应用和云应用
        applicationQuery.checkCanModify(menu.getApplicationId());
        // 验证菜单是否属于该应用
        if (!existing.getApplicationId().equals(menu.getApplicationId())) {
          throw ResourceNotFound.of("菜单「{0}」不存在", new Object[]{menu.getId()});
        }
        // 验证父菜单是否存在
        if (menu.getParentId() != null && menu.getParentId() > 0
            && !menu.getParentId().equals(menu.getId())) {
          applicationMenuQuery.findAndCheck(menu.getParentId());
        }
        // 验证菜单编码唯一性（排除自身）
        if (applicationMenuQuery.existsByAppIdAndCodeAndIdNot(
            menu.getApplicationId(), menu.getCode(), menu.getId())) {
          throw ResourceExisted.of("菜单编码「{0}」已存在", new Object[]{menu.getCode()});
        }
      }

      @Override
      protected ApplicationMenu process() {
        // 同步更新角色权限信息
        PermissionInfo existingPermission = existing.getPermission();
        PermissionInfo menuPermission = menu.getPermission();

        // 检查权限信息是否发生变化（需要处理null情况）
        boolean permissionChanged = false;
        if (existingPermission == null && menuPermission != null) {
          permissionChanged = true;
        } else if (existingPermission != null && menuPermission == null) {
          permissionChanged = true;
        } else if (existingPermission != null && !existingPermission.equals(menuPermission)) {
          permissionChanged = true;
        }

        if (permissionChanged && menuPermission != null) {
          List<Role> roles = roleQuery.findByAppId(existing.getApplicationId());
          if (!roles.isEmpty()) {
            for (Role role : roles) {
              if (isNotEmpty(role.getPermissions())) {
                boolean updated = false;
                for (PermissionInfo permission : role.getPermissions()) {
                  if (permission.getMenuId().equals(menu.getId())) {
                    // 更新权限信息
                    permission.setParentMenuId(menu.getParentId());
                    permission.setMenuName(menu.getName());
                    if (menuPermission.getResource() != null) {
                      permission.setResource(menuPermission.getResource());
                    }
                    if (menuPermission.getResourceName() != null) {
                      permission.setResourceName(menuPermission.getResourceName());
                    }
                    if (menuPermission.getActions() != null) {
                      permission.setActions(menuPermission.getActions());
                    }
                    updated = true;
                  }
                }
                if (updated) {
                  roleCmd.update0(role);
                }
              }
            }
          }
        }

        // 更新菜单
        CoreUtils.copyPropertiesIgnoreNull(menu, existing);

        // 确保权限信息存在并更新
        if (existing.getPermission() == null) {
          PermissionInfo newPermission = toPermissionInfo(menu, menuPermission);
          existing.setPermission(newPermission);
        } else {
          // 更新现有权限信息
          existing.getPermission().setMenuName(menu.getName());
          if (menuPermission != null) {
            existing.getPermission().setParentMenuId(menu.getParentId());
            if (menuPermission.getResource() != null) {
              existing.getPermission().setResource(menuPermission.getResource());
            }
            if (menuPermission.getResourceName() != null) {
              existing.getPermission().setResourceName(menuPermission.getResourceName());
            }
            if (menuPermission.getActions() != null) {
              existing.getPermission().setActions(menuPermission.getActions());
            }
          }
        }

        applicationMenuRepo.save(existing);

        // 记录操作日志
        String menuName = existing.getName() != null ? existing.getName() : existing.getCode();
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.APPLICATION,
            existing.getId(),
            menuName,
            OperationMessage.APPLICATION_MENU_UPDATE_DETAILS,
            new Object[]{menuName, existing.getApplicationId()}
        );

        return existing;
      }
    }.execute();
  }


  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long appId, Long menuId) {
    new BizTemplate<Void>(false) {
      ApplicationMenu menu;

      @Override
      protected void checkParams() {
        // 如果非运营租户，禁止修改来源是安装类型的应用和云应用
        applicationQuery.checkCanModify(appId);
        // 验证菜单是否存在且属于该应用
        menu = applicationMenuQuery.findAndCheck(menuId);
        if (!menu.getApplicationId().equals(appId)) {
          throw ResourceNotFound.of("菜单「{0}」不存在", new Object[]{menuId});
        }
      }

      @Override
      protected Void process() {
        // 保存菜单名称用于操作日志（删除前获取）
        String menuName = menu.getName() != null ? menu.getName() : menu.getCode();

        // 删除当前菜单
        applicationMenuRepo.deleteById(menuId);

        // 递归删除所有子菜单
        List<Long> subIds = new ArrayList<>();
        deleteChildrenRecursively(appId, menuId, subIds);

        // 同步删除角色权限信息
        List<Role> roles = roleQuery.findByAppId(appId);
        if (!roles.isEmpty()) {
          for (Role role : roles) {
            if (isNotEmpty(role.getPermissions())) {
              AtomicBoolean updated = new AtomicBoolean(false);
              role.getPermissions().removeIf(permission -> {
                if (permission.getMenuId().equals(menuId)
                    || subIds.contains(permission.getMenuId())) {
                  updated.set(true);
                  return true;
                }
                return false;
              });
              if (updated.get()) {
                roleCmd.update0(role);
              }
            }
          }
        }

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.APPLICATION,
            menuId,
            menuName,
            OperationMessage.APPLICATION_MENU_DELETE_DETAILS,
            new Object[]{menuName, appId}
        );

        return null;
      }
    }.execute();
  }

  /**
   * 递归删除所有子菜单
   */
  private void deleteChildrenRecursively(Long appId, Long parentId, List<Long> subIds) {
    List<ApplicationMenu> children = applicationMenuRepo.findByApplicationIdAndParentId(appId,
        parentId);
    if (children != null && !children.isEmpty()) {
      for (ApplicationMenu child : children) {
        // 递归删除子菜单的子菜单
        deleteChildrenRecursively(appId, child.getId(), subIds);
        // 删除当前子菜单
        subIds.add(child.getId());
        applicationMenuRepo.deleteById(child.getId());
      }
    }
  }

  @Override
  public void deleteByApplicationId(Long applicationId) {
    applicationMenuRepo.deleteByApplicationId(applicationId);
  }

  @Override
  protected BaseRepository<ApplicationMenu, Long> getRepository() {
    return applicationMenuRepo;
  }
}

